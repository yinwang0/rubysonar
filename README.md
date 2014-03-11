### RubySonar - a type inferencer and indexer for Ruby

RubySonar is a type inferencer and indexer for Ruby, which does sophisticated
interprocedural analysis to infer types. It is one of the underlying
technologies that powers the code search site <a
href="https://sourcegraph.com/github.com/rails/rails">Sourcegraph</a>.

RubySonar is modeled after <a
href="https://github.com/yinwang0/pysonar2">PySonar2</a>, which does a similar
analysis for Python and has been in use by Sourcegraph and Google. To understand
its technical properties, please refer to my blog posts:

- http://yinwang0.wordpress.com/2010/09/12/pysonar
- http://yinwang0.wordpress.com/2013/06/21/pysonar-slides


#### Demo

<img src="http://www.yinwang.org/images/rubysonar.gif" width="80%">



#### How to build

    mvn package



#### System Requirements

* irb

RubySonar uses the `irb` interpreter to parse Ruby code, so please make sure you
have it installed and pointed to by the `PATH` environment variable.



#### How to use

RubySonar is mainly designed as a library for IDEs and other developer tools, so
its interface may not be as appealing as an end-user tool, but for your
understanding of the library's capabilities, a reasonably nice demo program has
been built.

You can build a simple "code-browser" of your ruby code with the following
command line:

    java -jar target/rubysonar-0.1-SNAPSHOT.jar /path/to/project ./html

This will take a few minutes. You should find some interactive HTML files inside
the _html_ directory after this process.



#### License (GNU AGPLv3)

RubySonar - a type inferencer and indexer for Python

Copyright (c) 2013-2014 Yin Wang

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as
published by the Free Software Foundation, either version 3 of the
License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
