package rdfUtils

//import com.tinkerpop.blueprints.Vertex
//import com.tinkerpop.blueprints.impls.sail.SailTokens
import groovySparql.Sparql

import org.apache.jena.update.UpdateExecutionFactory
import org.apache.jena.update.UpdateFactory
import org.apache.jena.update.UpdateProcessor
import org.apache.jena.update.UpdateRequest
import org.apache.jena.rdf.model.Model
import org.apache.jena.rdf.model.ModelFactory
import org.apache.jena.sparql.modify.request.UpdateLoad
/*
new MarkupBuilder().root {
   a( a1:'one' ) {
     b { mkp.yield( '3 < 5' ) }
     c( a2:'two', 'blah' )
   }
 }
Will print the following to System.out:
<root>
   <a a1='one'>
     <b>3 &lt; 5</b>
     <c a2='two'>blah</c>
   </a>
 </root>
 */

/*
abstract class GGraph {
    RDFSlurper slurp
    def pipe

    GGraph(RDFSlurper slurp, pipe) {
        this.slurp = slurp
        this.pipe = pipe
    }

    def propertyMissing(String name) {
        getAt(name)
    }

    abstract def node()

    def collect(){
        node().value
    }

    def toList(){
        def lst = []
        node().value.fill(lst)
        lst
    }

    def findVertex(String name){
        def id = node().id.find{true}

        def q = slurp.prefixes +//"${g.namespaces['']}> \n" +
                "SELECT ?obj WHERE {<$id> <${slurp.toURI(name)}> ?obj.}"

        //println q

        /*def res = slurp.g.executeSparql(q)
        //println res
        if (!res.empty) return slurp.g.v(id).outE(slurp.toURI(name))

        q = slurp.prefixes +//<${g.namespaces['']}> \n" +
            "SELECT ?pred WHERE {<$id> ?pred ?z. ?pred rdfs:label \"$name\"@$slurp.lang}"

        res = slurp.g.executeSparql(q)
        if (res.empty)
            throw new RuntimeException("Unknown field: $name.")
        slurp.g.v(id).outE(res[0].pred.id)

    }

    def getAt(String name) {
        if (name[0]=='$') {
            def vertice = findVertex(slurp.toURI(name.substring(1)))
            def lst = []
            vertice.inV.fill(lst)
            def ret= lst.find{
                it.value.class != String || it.lang == slurp.lang
            }

            if (ret==null) throw new RuntimeException("No value for $name for language $slurp.lang")
            return ret.value
        }
        def edge1 = findVertex(name)
        new GEdge(slurp, edge1._())

    }

    abstract def getTriple(String propName)

    def putAt(String propName, obj2) {
        /*def (subj, pred, obj1) = getTriple(propName)

        if (pred!=null) {
            slurp.g.removeEdge(pred)
            if (obj1.outE().count() == 0)
                slurp.g.removeVertex(obj1)
        }
        slurp.g.addEdge(subj, slurp.addNode(obj2), slurp.toURI(propName))

    }
}


class GNode extends GGraph {

    def node() {pipe}

    GNode(RDFSlurper slurp, ver){
        super(slurp, ver)
    }

    def getTriple(String propName){
        def subj = node().find{true}
        def pred = subj.outE(slurp.toURI(propName)).find{true}
        def obj = pred?.outV.find{true}
        [subj, pred, obj]
    }
}

class GEdge extends GGraph {

    def getEdge() {pipe}

    GEdge(RDFSlurper slurp, edge){
        super(slurp, edge)
    }

    def node() {
        edge.inV
    }

    def getTriple(String propName){
        def subj = edge.inV.find{true}
        def pred = subj.outE(slurp.toURI(propName)).find{true}
        def obj = pred?.inV.find{true}
        [subj, pred, obj]
    }

    def leftShift(obj2) {
        def pred = edge.find{true}
        def subj = pred.outV.find{true}
        def obj1 = pred.inV.find{true}

        def propName = pred.label
        //println 'prop: '+propName

        slurp.g.removeEdge(pred)
        if (obj1.outE().count()==0)
            slurp.g.removeVertex(obj1)
        slurp.g.addEdge(subj, slurp.addNode(obj2), propName)
    }
}
*/
/**
 * Class RDFSlurper
 *
 * From: https://github.com/tinkerpop/blueprints/wiki/Sail-Implementation
 *
 * Each vertex is represented by an id: the string representation of the RDF
 * value being represented. For instance:
 *
 *  URI:  http://markorodriguez.com
 *  Blank Node:  _:A12345
 *  Literal:  "hello"^^<http://www.w3.org/2001/XMLSchema#string>
 *
 * They also have a kind property (kind) that is either uri, literal, or bnode.
 *
 * Only literal-based vertices have three other vertex properties.
 * Only two of which can be set.
 * These properties are the language (lang), the datatype (type), and the
 * typecasted value (value) of the literal label.
 * Note that in RDF, a literal can have either a language, a datatype, or neither.
 * Never can a literal have both a language and a datatype.
 * The typecasted value is the object created by the casting of the label of the
 * literal by its datatype.
 * The value of "6"^^<http://www.w3.org/2001/XMLSchema#int> is the integer 6.
 *
 * An edge represents an RDF statement. An edge id the string representation of
 * the RDF statement. For instance:
 *
 * Statement
 * (http://tinkerpop.com#marko, http://someontology.com#age, "30"^^<http://www.w3.org/2001/XMLSchema#int>)
 * (http://tinkerpop.com#marko, http://www.w3.org/2002/07/owl#sameAs, http://markorodrigue.com#marko) [http://tinkerpop.com#graph]
 *
 * Statements can only have a single property: the named graph property (ng).
 *
 */
//@CompileStatic
class RDFSlurper {

    /*static {
        Gremlin.load()
    }

    SailGraph g
    */

    String lang = 'en'
    //Logger log = Logger.getLogger(RDFSlurper.class);

    private Map<String, String> _prefixes = [:]
    private Sparql Sparql
    private String select = '*'

    RDFSlurper(){
        //g = new MemoryStoreSailGraph()
    }

    RDFSlurper(String endpoint, String update) {
        //g = new SparqlRepositorySailGraph(endpoint, update)
        //"http://localhost:8000/sparql/", "http://localhost:8000/update/")
        // SPARQL 1.0 or 1.1 endpoint
        Sparql = new Sparql(endpoint: endpoint)
    }

    RDFSlurper(String url){
        //g = new MemoryStoreSailGraph()
        //String url = 'http://bio.icmc.usp.br:9999/bigdata/namespace/sustenagro/sparql'
        //"http://localhost:8000/sparql/", "http://localhost:8000/update/")

        //g = new SparqlRepositorySailGraph(url, url)

        // SPARQL 1.0 or 1.1 endpoint
        Sparql = new Sparql(endpoint: url)

        addDefaultNamespaces()

        setLang('pt')



        //g2 = new BigdataGraphClient(url)

        //removeAll()
        //g.loadRDF(new FileInputStream(file), 'http://biomac.icmc.usp.br/sustenagro#', 'rdf-xml', null)
    }

    def removeAll(String data){
        delete("?s ?p ?o")
    }

    //    def sparql(String q) {
    //        def ret = []
    //        def f = prefixes + '\n' + q
    //        println f
    //        g.executeSparql(f).each{
    //            def map = [:]
    //            def add = true
    //            it.each {key, val->
    //                map[key] = val.value ? val.value : val.id
    //                if (val.lang != null && val.lang!=lang) add= false
    //            }
    //            if (add) ret.add(map)
    //        }
    //        ret
    //    }

    /*
    def query1(String q) {
        def ret = []
        def f = prefixes + '\n select * where {' + q +'}'
        g.executeSparql(f).each{
            def map = [:]
            def add = true
            it.each {key, val->
                map[key] = val.value ? val.value : val.id
                if (val.lang != null && val.lang!=lang) add= false
            }
            if (add) ret.add(map)
        }
        ret
    }
    */

    def select(str){
        select = str
        this
    }

    def query(String q, String order='', String lang = this.lang) {
        def f = "$prefixes \nselect $select where {$q} ${order}"
        select = '*'
        //println f+"\n"
        Sparql.query(f, lang)
    }

    def insert(String q, String lang = this.lang){
        def f = "$prefixes \nINSERT DATA {$q}"
        Sparql.update(f)
    }

    def delete(String q){
        def f = "$prefixes \n DELETE where {$q}"
        Sparql.update(f)
    }

    def update(String q){
        def f = "$prefixes \n $q"
        Sparql.update(f)
    }

    def loadRDF(InputStream is){
        Model m = ModelFactory.createDefaultModel()

        m.read(is, "http://bio.icmc.usp.br/sustenagro#")

        UpdateRequest request = UpdateFactory.create()

        request.add(new UpdateLoad("http://java.icmc.usp.br/sustenagro/SustenAgroOntology.rdf", "http://bio.icmc.usp.br/sustenagro#"))

        UpdateProcessor processor = UpdateExecutionFactory.createRemoteForm(request, 'http://java.icmc.usp.br:9999/bigdata/namespace/kb/sparql')

        processor.execute()
    }

    def addDefaultNamespaces() {
        addNamespace('rdf','http://www.w3.org/1999/02/22-rdf-syntax-ns#')
        addNamespace('rdfs','http://www.w3.org/2000/01/rdf-schema#')
        addNamespace('owl','http://www.w3.org/2002/07/owl#')
        addNamespace('xsd','http://www.w3.org/2001/XMLSchema#')
        addNamespace('foaf','http://xmlns.com/foaf/0.1/')
        addNamespace('dc','http://purl.org/dc/terms/')
        addNamespace('dbp','http://dbpedia.org/ontology/')
        addNamespace('','http://bio.icmc.usp.br/sustenagro#')
    }

    def addNamespace(String prefix, String namespace){
        _prefixes.put(prefix, namespace)
        // Method not working with SparqlRepositorySailGraph
        //g.addNamespace(prefix, namespace)
    }

    def toURI(String uri){
        if (uri==null || uri == '' ) return null
        if (uri.contains(' ')) return null
        if (uri.startsWith('_:')) return uri
        if (uri[0]==':') return _prefixes['']+uri.substring(1)
        if (uri.startsWith('http:')) return uri
        if (uri.startsWith('urn:')) return uri
        if (!uri.contains(':')) return _prefixes['']+ uri
        def pre = _prefixes[uri.tokenize(':')[0]]
        if (pre==null) return uri
        return pre+uri.substring(uri.indexOf(':')+1)
    }

    def fromURI(String uri){
        if (uri==null) return null
        if (uri.startsWith('_:')) return uri
        if (!uri.startsWith('http:')) return uri
        def v = _prefixes.find { key, obj ->
            uri.startsWith(obj)
        }
        v.key + ':' + uri.substring(v.value.size())
    }

    def getPrefixes(){
        def str = ''
        _prefixes.each {key, obj -> str += "PREFIX $key: <$obj>\n"}
        str
    }

    def findVertex(String name){

        def q = prefixes + "SELECT ?subj WHERE {?subj rdfs:label \"$name\"@$lang}"

        //println q
        def res = g.executeSparql(q)
        if (res.empty)
            throw new RuntimeException("Unknown Element: $name.")
        //println res[0].subj.id
        g.v(res[0].subj.id)
    }

    /*def propertyMissing(String name) {
        getAt(name)
    }
    */

    def v(String name){
        g.v(toURI(name))
    }

    /*
    def getAt(String name) {
        def node = g.v(toURI(name))

        //println "uri:"+toURI(name)

        if (node.both.count()==0) {
            g.removeVertex(node)
            node = findVertex(name)
        }
        new GNode(this, node._())
    }
    */

    static N(Map node) {node}

    static N(Map node, String uri) {
        [uri, node]
    }

    def addNode(node) {

        def addProps = { uri, map ->
            def v2 = g.addVertex(toURI(uri))
            map.each {key, value ->
                g.addEdge(v2, addNode(value), toURI(key))
            }
            v2
        }
        switch (node) {
            case Vertex:
                return node
            case List:
                return addProps(node[0], node[1])
            case Map:
                return addProps(
                        //'_:Z' + ((node.size() * 100000000 + 7652526535345544) * Math.random()).toLong(),
                        //'http://blankNode.org/blank/B' + ((node.size() * 100000000 + 7652526535345544) * Math.random()).toLong(),
                        null,
                        node)
            case String:
                return g.addVertex('"' + node + '"@' + lang)
            case int: case Integer: case long: case Long: case BigInteger:
                return g.addVertex('"' + node + '"^^<http://www.w3.org/2001/XMLSchema#integer>')
            case float: case Float: case double: case Double: case BigDecimal:
                return g.addVertex('"' + node + '"^^<http://www.w3.org/2001/XMLSchema#double>')
            case boolean:
                return g.addVertex('"' + node + '"^^<http://www.w3.org/2001/XMLSchema#boolean>')
        }
    }

    def dataSchema(value){
        def result = null
        switch (value) {
            case String:
                result = '"' + node + '"@' + lang
                break
            case int: case Integer: case long: case Long: case BigInteger:
                result = '"' + node + '"^^<http://www.w3.org/2001/XMLSchema#integer>'
                break
            case float: case Float: case double: case Double: case BigDecimal:
                result = '"' + node + '"^^<http://www.w3.org/2001/XMLSchema#double>'
                break
            case boolean:
                result = '"' + node + '"^^<http://www.w3.org/2001/XMLSchema#boolean>'
                break
        }
        return result
    }

    def existOntology(String uri){
        def existOnt = false
        def result = query("?o rdf:type owl:Ontology")

        result.each{
            if(it.o == uri)
                existOnt = true
        }

        existOnt
    }

}