/*
 * Copyright (c) 2015-2017 Dilvan Moreira.
 * Copyright (c) 2015-2017 John Garavito.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package dsl

import org.codehaus.groovy.control.CompilerConfiguration
import org.kohsuke.groovy.sandbox.SandboxTransformer
//import org.pcollections.PSet
import org.springframework.context.ApplicationContext
import semantics.DataReader
import org.organicdesign.fp.collections.*
import static org.organicdesign.fp.StaticImports.*

/**
 * AQDMDSL
 *
 * @author Dilvan Moreira.
 */

class DSLanguage {
    private final filename
    private final _ctx
    private final _k

    private final _sandbox
    // Interpreter in groovy
    private final dslInter

    private _props = [:]

    /**
     *
     * @param filename
     * @param applicationContext
     */
    DSLanguage(final String filename, final ApplicationContext applicationContext){
        // Create CompilerConfiguration and assign
        // the DelegatingScript class as the base script class.
        _ctx = applicationContext
        _k = _ctx.getBean('k')
        // Traz os tipos que estão da DSL de tipos
        //_gui = _ctx.getBean('gui')
        // Library Markdown
        //_md = _ctx.getBean('md')
        // Internationalization
        //_msg = _ctx.getBean('messageSource')

        this.filename = filename

        // Instanciar a DSL
        def _cc = new CompilerConfiguration()
        // How the sandbox will behave
        _cc.addCompilationCustomizers(new SandboxTransformer())
        // It will be delegated to a class
        _cc.setScriptBaseClass(DelegatingScript.class.name)


        dslInter = new GroovyShell(new Binding(), _cc)
        // The sandbox used by the program
        _sandbox = new DSLSandbox()

        reload()
    }

    /**
     * Reload the DSL if program change (without restarting
     * application)
     * @param code
     * @return
     */
    def reload(){
        _props= [:]
        _sandbox.register()

        def script = (DelegatingScript) dslInter.parse(_ctx.getResource(filename).file)
        script.setDelegate(this)
        //println "No property used yet."

        def response  = [:]

        // Run DSL script.
        try {
            script.run()
            response.status = 'ok'
        }
        catch(Exception e){
            response.error = [:]
            for (StackTraceElement el : e.stackTrace) {
                if(el.methodName == 'run' && el.fileName ==~ /Script.+\.groovy/) {
                    response.error.line = el.lineNumber
                    response.error.message = e.message
                    response.error.filename = el.fileName
                }
            }
            response.status = 'error'
        }
        finally {
            _sandbox.unregister()
        }
        return response
    }

    def propertyMissing(String key) {
        println "Property missing: $key"

        //throw new Exception("propertyMissing: $key")
    }

    def propertyMissing(String key, arg) {
        _props[key] = arg
        //println "Property missing: $key -> $arg"
    }

    def methodMissing(String key, attrs){
        throw new Exception("Method missing: $key -> $attrs")
    }

    /**
     * Message in locale language.
     *
     * @param code
     * @return
     */
    def message(String code){
        _ctx.getBean('messageSource').getMessage(code, null, Locale.getDefault())
    }

    //static _toHTML(String txt) {_md.markdownToHtml(txt)}
}

class ADMDSL extends DSLanguage {
    def _filter = [:]

    ADMDSL(final String filename, final ApplicationContext applicationContext){
        super(filename, applicationContext)
    }

    def methodMissing(String key, attrs){
        println("Method missing: $key -> $attrs")

//        ImList t = vec(vec("Jane", "Smith", vec("a@b.c", "b@c.d")),
//                tup("Fred", "Tase",  vec("c@d.e", "d@e.f", "e@f.g")))
//        println t
//        ImMap d = map(tup("Jane", vec("a@b.c", "b@c.d")),
//                tup("Tase",  vec("c@d.e", "d@e.f", "e@f.g")))
//        def e= d.assoc('ghhr', vec('kjlkjk kjkjk', 'uuuuiuueyyehhe'))
//        println e
//        println d
    }

    def filter(attrs){
        _filter = attrs
    }
}

