package org.yinwang.rubysonar;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yinwang.rubysonar.ast.Call;
import org.yinwang.rubysonar.ast.Node;
import org.yinwang.rubysonar.ast.Url;
import org.yinwang.rubysonar.types.FunType;
import org.yinwang.rubysonar.types.ModuleType;
import org.yinwang.rubysonar.types.Type;

import java.io.File;
import java.net.URL;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Analyzer {

    // global static instance of the analyzer itself
    public static String MODEL_LOCATION = "org/yinwang/rubysonar/models";
    public static Analyzer self;
    public String sid = _.newSessionId();
    public boolean debug = false;

    public State moduleTable = new State(null, State.StateType.GLOBAL);
    public List<String> loadedFiles = new ArrayList<>();
    public State globaltable = new State(null, State.StateType.GLOBAL);
    public List<Binding> allBindings = new ArrayList<>();
    private Map<Node, List<Binding>> references = new LinkedHashMap<>();
    public Map<String, List<Diagnostic>> semanticErrors = new HashMap<>();
    public Map<String, List<Diagnostic>> parseErrors = new HashMap<>();
    public String cwd = null;
    public int nCalled = 0;
    public boolean multilineFunType = false;
    public List<String> path = new ArrayList<>();
    private Set<FunType> uncalled = new HashSet<>();
    private Set<Object> callStack = new HashSet<>();
    private Set<Object> importStack = new HashSet<>();

    private AstCache astCache;
    public String cacheDir;
    public Set<String> failedToParse = new HashSet<>();
    public Stats stats = new Stats();
    private Logger logger;
    private Progress loadingProgress = null;

    public String projectDir;
    public String suffix;

    public Map<String, Object> options;


    public Analyzer() {
        this(null);
    }


    public Analyzer(Map<String, Object> options) {
        self = this;
        if (options != null) {
            this.options = options;
        } else {
            this.options = new HashMap<>();
        }
        stats.putInt("startTime", System.currentTimeMillis());
        logger = Logger.getLogger(Analyzer.class.getCanonicalName());
        this.suffix = ".rb";
        addPythonPath();
        copyModels();
        createCacheDir();
        getAstCache();
    }


    public boolean hasOption(String option) {
        Object op = options.get(option);
        if (op != null && op.equals(true)) {
            return true;
        } else {
            return false;
        }
    }


    private void copyModels() {
        URL resource = Thread.currentThread().getContextClassLoader().getResource(MODEL_LOCATION);
        String dest = _.locateTmp("models");
        try {
            _.copyResourcesRecursively(resource, new File(dest));
        } catch (Exception e) {
            _.die("Failed to copy models. Please check permissions of writing to: " + dest);
        }
        addPath(dest);
    }


    // main entry to the analyzer
    public void analyze(String path) {
        String upath = _.unifyPath(path);
        File f = new File(upath);
        projectDir = f.isDirectory() ? f.getPath() : f.getParent();
        loadFileRecursive(upath);
    }


    public void setCWD(String cd) {
        if (cd != null) {
            cwd = _.unifyPath(cd);
        }
    }


    public void addPaths(@NotNull List<String> p) {
        for (String s : p) {
            addPath(s);
        }
    }


    public void addPath(String p) {
        path.add(_.unifyPath(p));
    }


    public void setPath(@NotNull List<String> path) {
        this.path = new ArrayList<>(path.size());
        addPaths(path);
    }


    private void addPythonPath() {
        String path = System.getenv("PYTHONPATH");
        if (path != null) {
            String[] segments = path.split(":");
            for (String p : segments) {
                addPath(p);
            }
        }
    }


    @NotNull
    public List<String> getLoadPath() {
        List<String> loadPath = new ArrayList<>();
        if (cwd != null) {
            loadPath.add(cwd);
        }
        if (projectDir != null && (new File(projectDir).isDirectory())) {
            loadPath.add(projectDir);
        }
        loadPath.addAll(path);
        return loadPath;
    }


    public boolean inStack(Object f) {
        return callStack.contains(f);
    }


    public void pushStack(Object f) {
        callStack.add(f);
    }


    public void popStack(Object f) {
        callStack.remove(f);
    }


    public boolean inImportStack(Object f) {
        return importStack.contains(f);
    }


    public void pushImportStack(Object f) {
        importStack.add(f);
    }


    public void popImportStack(Object f) {
        importStack.remove(f);
    }


    @NotNull
    public List<Binding> getAllBindings() {
        return allBindings;
    }


    @Nullable
    ModuleType getCachedModule(String file) {
        Type t = moduleTable.lookupType(_.moduleQname(file));
        if (t == null) {
            return null;
        } else if (t.isUnionType()) {
            for (Type tt : t.asUnionType().getTypes()) {
                if (tt.isModuleType()) {
                    return (ModuleType) tt;
                }
            }
            return null;
        } else if (t.isModuleType()) {
            return (ModuleType) t;
        } else {
            return null;
        }
    }


    public List<Diagnostic> getDiagnosticsForFile(String file) {
        List<Diagnostic> errs = semanticErrors.get(file);
        if (errs != null) {
            return errs;
        }
        return new ArrayList<>();
    }


    public void putRef(@NotNull Node node, @NotNull List<Binding> bs) {
        if (!(node instanceof Url)) {
            List<Binding> bindings = references.get(node);
            if (bindings == null) {
                bindings = new ArrayList<>(1);
                references.put(node, bindings);
            }
            for (Binding b : bs) {
                if (!bindings.contains(b)) {
                    bindings.add(b);
                }
                b.addRef(node);
            }
        }
    }


    public void putRef(@NotNull Node node, @NotNull Binding b) {
        List<Binding> bs = new ArrayList<>();
        bs.add(b);
        putRef(node, bs);
    }


    @NotNull
    public Map<Node, List<Binding>> getReferences() {
        return references;
    }


    public void putProblem(@NotNull Node loc, String msg) {
        String file = loc.file;
        if (file != null) {
            addFileErr(file, loc.start, loc.end, msg);
        }
    }


    // for situations without a Node
    public void putProblem(@Nullable String file, int begin, int end, String msg) {
        if (file != null) {
            addFileErr(file, begin, end, msg);
        }
    }


    void addFileErr(String file, int begin, int end, String msg) {
        Diagnostic d = new Diagnostic(file, Diagnostic.Category.ERROR, begin, end, msg);
        getFileErrs(file, semanticErrors).add(d);
    }


    List<Diagnostic> getParseErrs(String file) {
        return getFileErrs(file, parseErrors);
    }


    List<Diagnostic> getFileErrs(String file, @NotNull Map<String, List<Diagnostic>> map) {
        List<Diagnostic> msgs = map.get(file);
        if (msgs == null) {
            msgs = new ArrayList<>();
            map.put(file, msgs);
        }
        return msgs;
    }


    @Nullable
    public Type loadFile(String path) {
//        Util.msg("loading: " + path);

        path = _.unifyPath(path);
        File f = new File(path);

        if (!f.canRead()) {
            return null;
        }

        Type module = getCachedModule(path);
        if (module != null) {
            return module;
        }

        // detect circular import
        if (Analyzer.self.inImportStack(path)) {
            return null;
        }

        // set new CWD and save the old one on stack
        String oldcwd = cwd;
        setCWD(f.getParent());

        Analyzer.self.pushImportStack(path);
        Type type = parseAndResolve(path);

        // restore old CWD
        setCWD(oldcwd);
        Analyzer.self.popImportStack(path);

        return type;
    }


    private boolean isInLoadPath(File dir) {
        for (String s : getLoadPath()) {
            if (new File(s).equals(dir)) {
                return true;
            }
        }
        return false;
    }


    @Nullable
    private Type parseAndResolve(String file) {
        loadingProgress.tick();

        try {
            Node ast = getAstForFile(file);

            if (ast == null) {
                failedToParse.add(file);
                return null;
            } else {
                Type type = Node.transformExpr(ast, globaltable);
                loadedFiles.add(file);
                return type;
            }
        } catch (OutOfMemoryError e) {
            if (astCache != null) {
                astCache.clear();
            }
            System.gc();
            return null;
        }
    }


    private void createCacheDir() {
        cacheDir = _.makePathString(_.getSystemTempDir(), "rubysonar", "ast_cache");
        File f = new File(cacheDir);
        _.msg("AST cache is at: " + cacheDir);

        if (!f.exists()) {
            if (!f.mkdirs()) {
                _.die("Failed to create tmp directory: " + cacheDir +
                        ".Please check permissions");
            }
        }
    }


    private AstCache getAstCache() {
        if (astCache == null) {
            astCache = AstCache.get();
        }
        return astCache;
    }


    @Nullable
    public Node getAstForFile(String file) {
        return getAstCache().getAST(file);
    }


    public Type requireFile(String headName) {
        List<String> loadPath = getLoadPath();

        for (String p : loadPath) {
            String trial = _.makePathString(p, headName + suffix);
            if (new File(trial).exists()) {
                return loadFile(trial);
            }
        }

        return null;
    }


    public void loadFileRecursive(String fullname) {
        int count = countFileRecursive(fullname);
        if (loadingProgress == null) {
            loadingProgress = new Progress(count, 50);
        }

        File file_or_dir = new File(fullname);

        if (file_or_dir.isDirectory()) {
            for (File file : file_or_dir.listFiles()) {
                loadFileRecursive(file.getPath());
            }
        } else {
            if (file_or_dir.getPath().endsWith(suffix)) {
                loadFile(file_or_dir.getPath());
            }
        }
    }


    // count number of files that need processing
    public int countFileRecursive(String fullname) {
        File file_or_dir = new File(fullname);
        int sum = 0;

        if (file_or_dir.isDirectory()) {
            for (File file : file_or_dir.listFiles()) {
                sum += countFileRecursive(file.getPath());
            }
        } else {
            if (file_or_dir.getPath().endsWith(suffix)) {
                sum += 1;
            }
        }
        return sum;
    }


    public void finish() {
//        progress.end();
        _.msg("\nFinished loading files. " + nCalled + " functions were called.");
        _.msg("Analyzing uncalled functions");
        applyUncalled();

        // mark unused variables
        for (Binding b : allBindings) {
            if (!b.getType().isClassType() &&
                    !b.getType().isFuncType() &&
                    !b.getType().isModuleType()
                    && b.getRefs().isEmpty())
            {
                Analyzer.self.putProblem(b.node, "Unused variable: " + b.getName());
            }
        }

        _.msg(getAnalysisSummary());
    }


    public void close() {
        astCache.close();
    }


    public void addUncalled(@NotNull FunType cl) {
        if (!cl.func.called) {
            uncalled.add(cl);
        }
    }


    public void removeUncalled(FunType f) {
        uncalled.remove(f);
    }


    public void applyUncalled() {
        Progress progress = new Progress(uncalled.size(), 50);

        while (!uncalled.isEmpty()) {
            List<FunType> uncalledDup = new ArrayList<>(uncalled);

            for (FunType cl : uncalledDup) {
                progress.tick();
                Call.apply(cl, null, null, null, null, null, null);
            }
        }
    }


    @NotNull
    public String getAnalysisSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n" + _.banner("analysis summary"));

        String duration = _.formatTime(System.currentTimeMillis() - stats.getInt("startTime"));
        sb.append("\n- total time: " + duration);
        sb.append("\n- modules loaded: " + loadedFiles.size());
        sb.append("\n- semantic problems: " + semanticErrors.size());
        sb.append("\n- failed to parse: " + failedToParse.size());

        // calculate number of defs, refs, xrefs
        int nDef = 0, nXRef = 0;
        for (Binding b : getAllBindings()) {
            nDef += 1;
            nXRef += b.getRefs().size();
        }

        sb.append("\n- number of definitions: " + nDef);
        sb.append("\n- number of cross references: " + nXRef);
        sb.append("\n- number of references: " + getReferences().size());

        long resolved = stats.getInt("resolved");
        long unresolved = stats.getInt("unresolved");
        sb.append("\n- resolved names: " + resolved);
        sb.append("\n- unresolved names: " + unresolved);
        sb.append("\n- name resolve rate: " + _.percent(resolved, resolved + unresolved));
        sb.append("\n" + _.getGCStats());

        return sb.toString();
    }


    @NotNull
    public List<String> getLoadedFiles() {
        List<String> files = new ArrayList<>();
        for (String file : loadedFiles) {
            if (file.endsWith(suffix)) {
                files.add(file);
            }
        }
        return files;
    }


    public void registerBinding(@NotNull Binding b) {
        allBindings.add(b);
    }


    public void log(Level level, String msg) {
        if (logger.isLoggable(level)) {
            logger.log(level, msg);
        }
    }


    @NotNull
    @Override
    public String toString() {
        return "(analyzer:locs=" + references.size() + ":files=" + loadedFiles.size() + ")";
    }
}
