package org.yinwang.rubysonar;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.google.common.collect.Lists;
import org.jetbrains.annotations.NotNull;
import org.yinwang.rubysonar.ast.Function;
import org.yinwang.rubysonar.ast.Node;
import org.yinwang.rubysonar.ast.Str;
import org.yinwang.rubysonar.types.Type;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;


public class JSONDump {

    private static Logger log = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private static Set<String> seenDef = new HashSet<>();
    private static Set<String> seenRef = new HashSet<>();
    private static Set<String> seenDocs = new HashSet<>();


    @NotNull
    private static String dirname(@NotNull String path) {
        File f = new File(path);
        if (f.getParent() != null) {
            return f.getParent();
        } else {
            return path;
        }
    }


    private static Analyzer newAnalyzer(List<String> srcpath, String[] inclpaths) throws Exception {
        Analyzer idx = new Analyzer();
        for (String inclpath : inclpaths) {
            idx.addPath(inclpath);
        }

        idx.analyze(srcpath);
        idx.finish();

        if (idx.semanticErrors.size() > 0) {
            log.info("Analyzer errors:");
            for (Entry<String, List<Diagnostic>> entry : idx.semanticErrors.entrySet()) {
                String k = entry.getKey();
                log.info("  Key: " + k);
                List<Diagnostic> diagnostics = entry.getValue();
                for (Diagnostic d : diagnostics) {
                    log.info("    " + d);
                }
            }
        }

        return idx;
    }


    private static String kindName(Binding.Kind kind) {
        if (kind == Binding.Kind.CLASS) {
            return "class";
        } else if (kind == Binding.Kind.CLASS_METHOD) {
            return "method";
        } else if (kind == Binding.Kind.METHOD) {
            return "method";
        } else {
            return kind.toString().toLowerCase();
        }
    }


    private static void writeSymJson(@NotNull Binding binding, JsonGenerator json) throws IOException {
        if (binding.start < 0) {
            return;
        }

        String name = binding.node.name;
        boolean isExported = !(
                Binding.Kind.VARIABLE == binding.kind ||
                        Binding.Kind.PARAMETER == binding.kind ||
                        Binding.Kind.SCOPE == binding.kind ||
                        Binding.Kind.ATTRIBUTE == binding.kind ||
                        (name != null && (name.length() == 0 || name.startsWith("lambda%"))));

        String path = binding.qname.replace("%20", ".");

        if (!seenDef.contains(path)) {
            seenDef.add(path);

            json.writeStartObject();
            json.writeStringField("name", name);
            json.writeStringField("path", path);
            json.writeStringField("file", binding.file);
            json.writeNumberField("identStart", binding.start);
            json.writeNumberField("identEnd", binding.end);
            json.writeNumberField("defStart", binding.bodyStart);
            json.writeNumberField("defEnd", binding.bodyEnd);
            json.writeBooleanField("exported", isExported);
            json.writeStringField("kind", kindName(binding.kind));

            if (binding.kind == Binding.Kind.METHOD || binding.kind == Binding.Kind.CLASS_METHOD) {
                // get args expression
                Type t = binding.type;

                if (t.isUnionType()) {
                    t = t.asUnionType().firstUseful();
                }

                if (t != null && t.isFuncType()) {
                    Function func = t.asFuncType().func;
                    if (func != null) {
                        String signature = func.getArgList();
                        if (!signature.equals("")) {
                            signature = "(" + signature + ")";
                        }
                        json.writeStringField("signature", signature);
                    }
                }
            }

            Str docstring = binding.findDocString();
            if (docstring != null) {
                json.writeStringField("docstring", docstring.value);
            }

            json.writeEndObject();
        }
    }


    private static void writeRefJson(Node ref, Binding binding, JsonGenerator json) throws IOException {
        if (binding.file != null) {
            String path = binding.qname.replace("%20", ".");

            if (binding.start >= 0 && ref.start >= 0) {
                json.writeStartObject();
                json.writeStringField("sym", path);
                json.writeStringField("symOrigin", binding.node.file);
                json.writeStringField("file", ref.file);
                json.writeNumberField("start", ref.start);
                json.writeNumberField("end", ref.end);
                json.writeBooleanField("builtin", false);
                json.writeEndObject();
            }
        }
    }


    private static void writeDocJson(Binding binding, Analyzer idx, JsonGenerator json) throws Exception {
        String path = binding.qname.replace("%20", ".");

        if (!seenDocs.contains(path)) {
            seenDocs.add(path);

            Str doc = null;
            if (binding.node instanceof org.yinwang.rubysonar.ast.Class) {
                doc = ((org.yinwang.rubysonar.ast.Class) binding.node).docstring;
            } else if (binding.node instanceof Function) {
                doc = ((Function) binding.node).docstring;
            }
            if (doc != null) {
                json.writeStartObject();
                json.writeStringField("sym", path);
                json.writeStringField("file", binding.file);
                json.writeStringField("body", doc.value);
                json.writeNumberField("start", doc.start);
                json.writeNumberField("end", doc.end);
                json.writeEndObject();
            }
        }
    }


    private static boolean shouldEmit(@NotNull String pathToMaybeEmit, String srcpath) {
        return _.unifyPath(pathToMaybeEmit).startsWith(_.unifyPath(srcpath));
    }


    static int neMethods = 0;
    static int neFunc = 0;
    static int neClass = 0;


    /*
     * Precondition: srcpath and inclpaths are absolute paths
     */
    private static void graph(List<String> srcpath,
                              String[] inclpaths,
                              OutputStream symOut,
                              OutputStream refOut,
                              OutputStream docOut) throws Exception
    {
        // Compute parent dirs, sort by length so potential prefixes show up first
        List<String> parentDirs = Lists.newArrayList(inclpaths);
        Collections.sort(parentDirs, new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                int diff = s1.length() - s2.length();
                if (0 == diff) {
                    return s1.compareTo(s2);
                }
                return diff;
            }
        });

        Analyzer idx = newAnalyzer(srcpath, inclpaths);
        idx.multilineFunType = true;
        JsonFactory jsonFactory = new JsonFactory();
        JsonGenerator symJson = jsonFactory.createGenerator(symOut);
        JsonGenerator refJson = jsonFactory.createGenerator(refOut);
        JsonGenerator docJson = jsonFactory.createGenerator(docOut);
        JsonGenerator[] allJson = {symJson, refJson, docJson};
        for (JsonGenerator json : allJson) {
            json.writeStartArray();
        }

        Set<String> srcpathSet = new HashSet<>();
        srcpathSet.addAll(srcpath);

        for (Binding b : idx.getAllBindings()) {

            if (b.file != null) {
                writeSymJson(b, symJson);
            }

            for (Node ref : b.refs) {
                if (ref.file != null) {
                    String key = ref.file + ":" + ref.start;
                    if (!seenRef.contains(key)) {
                        writeRefJson(ref, b, refJson);
                        seenRef.add(key);
                    }
                }
            }
            writeRefJson(b.node, b, refJson);
        }

        for (JsonGenerator json : allJson) {
            json.writeEndArray();
            json.close();
        }
    }


    private static void info(Object msg) {
        System.out.println(msg);
    }


    private static void usage() {
        info("Usage: java org.yinwang.rubysonar.dump <source-path> <include-paths> <out-root> [verbose]");
        info("  <source-path> is path to source unit (package directory or module file) that will be graphed");
        info("  <include-paths> are colon-separated paths to included libs");
        info("  <out-root> is the prefix of the output files.  There are 3 output files: <out-root>-doc, <out-root>-sym, <out-root>-ref");
        info("  [verbose] if set, then verbose logging is used (optional)");
    }


    public static void main(String[] args) throws Exception {
        log.setLevel(Level.SEVERE);

        String[] inclpaths;
        String outroot;
        List<String> srcpath = new ArrayList<>();

        if (args.length >= 2) {
            outroot = args[0];
            inclpaths = args[1].split(":");
            srcpath.addAll(Arrays.asList(args).subList(2, args.length));
        } else {
            usage();
            return;
        }

        String symFilename = outroot + "-sym";
        String refFilename = outroot + "-ref";
        String docFilename = outroot + "-doc";
        OutputStream symOut = null, refOut = null, docOut = null;
        try {
            docOut = new BufferedOutputStream(new FileOutputStream(docFilename));
            symOut = new BufferedOutputStream(new FileOutputStream(symFilename));
            refOut = new BufferedOutputStream(new FileOutputStream(refFilename));
            _.msg("graphing: " + srcpath);
            graph(srcpath, inclpaths, symOut, refOut, docOut);
            docOut.flush();
            symOut.flush();
            refOut.flush();
        } catch (FileNotFoundException e) {
            System.err.println("Could not find file: " + e);
            return;
        } finally {
            if (docOut != null) {
                docOut.close();
            }
            if (symOut != null) {
                symOut.close();
            }
            if (refOut != null) {
                refOut.close();
            }
        }
        log.info("SUCCESS");
    }
}
