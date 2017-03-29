/*
 * Copyright (c) 2015-2016 Dilvan Moreira.
 * Copyright (c) 2015-2016 John Garavito.
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

package semantics

import groovySparql.SparqlBase

/**
 * Know
 *
 * @author Dilvan Moreira.
 * @author John Garavito.
 */
class Know {

    private Map _prefixes = [:]
    private SparqlBase sparql
    private String select = '*'
    private String lang = 'en'
    def DSL = [:]

    Know(String url){
        sparql = new SparqlBase(endpoint: url)
        addDefaultNamespaces()
        lang = 'pt'
    }

    private addDefaultNamespaces(){
        addNamespace('rdf','http://www.w3.org/1999/02/22-rdf-syntax-ns#')
        addNamespace('rdfs','http://www.w3.org/2000/01/rdf-schema#')
        addNamespace('owl','http://www.w3.org/2002/07/owl#')
        addNamespace('xsd','http://www.w3.org/2001/XMLSchema#')
        addNamespace('foaf','http://xmlns.com/foaf/0.1/')
        //addNamespace('dcterm', 'http://purl.org/dc/terms/')

        addNamespace('dc','http://purl.org/dc/terms/')
        addNamespace('dbp','http://dbpedia.org/ontology/')
        addNamespace('dbpr','http://dbpedia.org/resource/')
        addNamespace('pt','http://dbpedia.org/property/pt/')
        addNamespace('ui','http://purl.org/biodiv/semanticUI#')
        addNamespace('inds','http://semantic.icmc.usp.br/individuals#')
        addNamespace('','http://semantic.icmc.usp.br/sustenagro#')
    }

    def addNamespace(String prefix, String namespace){
        _prefixes.put(prefix, namespace)
    }

    def propertyMissing(String name){
        getAt(name)
    }

    Node getAt(String name){
        findNode(name)
    }

    private Node findNode(String name){
        new Node(this, toURI(name))
    }

    Know select(str){
        select = str
        this
    }

    def query(String q, String order = '', String lang = this.lang){
        def f = "$prefixes \nselect $select where {$q} ${order}"
        select = '*'
        sparql.query(f, lang)
    }

    def insert(String q) { //}, String lang = this.lang){
        def f = "$prefixes \nINSERT DATA {$q}"
        sparql.update(f)
    }

    def delete(String q, String d=''){
        def f = "$prefixes \n DELETE $d WHERE {$q}"
        sparql.update(f)
    }

    def update(String q){
        def f = "$prefixes \n $q"
        sparql.update(f)
    }

//    def removeAll(String data){
//        delete('?s ?p ?o')
//    }

    String toURI(String id){
        if (id==null || id == '') return null
        if (id == ':') return _prefixes['']
        if (!id.contains(' ')){
            if (id.startsWith('_:')) return id
            if (id.startsWith('http:')) return id
            if (id.startsWith('urn:')) return id
            if (id.startsWith(':')) return _prefixes['']+id.substring(1)
            if (id.contains(':')){
                def prefix = _prefixes[id.split(':')[0]]
                if(prefix?.trim())
                    return prefix+id.substring(id.indexOf(':')+1)
                return null
            }
            println 'prexixes analyse: '+id
            if (!id.contains(':')) return searchByLabel(id)
        } else
            return null
    }

//    def fromURI(String uidri){
//        if (id==null) return null
//        if (id.startsWith('_:')) return id
//        if (!id.startsWith('http:')) return id
//        def v = _prefixes.find { key, obj ->
//            id.startsWith(obj)
//        }
//        v.key + ':' + id.substring(v.value.size())
//    }

    def shortURI(String id){
        if (id==null || id == '' ) return null
        if (!id.contains(' ')){
            if (id.startsWith('_:')) return id.substring(2)
            if (id.startsWith(':')) return '-'+id.substring(1)
            if (id.startsWith('http:') || !id.contains(':')){
                def prefix = searchPrefix(id)
                return prefix.alias+'-'+id.replace(prefix.uri,'')
            }
        } else
            return null
    }

//    def shortToURI(String id){
//        if (id==null || id == '' ) return null
//        if (!id.contains(' ')){
//            if (id.startsWith('_:') || id.startsWith('http:') || id.startsWith('urn:')) return id
//            if (id.startsWith(':')) return toURI(id)
//            if (id.contains('-')){
//                def prefix = searchPrefix(id)
//                return prefix.uri+id.substring(id.indexOf('-')+1)
//            } else
//                return '_:'+id
//        } else
//            return null
//    }

    def searchByLabel(String name){
        println "Heavy costly!"
        def langs = ['en', 'pt', 'es', 'fr', 'de']

        langs.findResult('') {
            def search = query("?uri rdfs:label '" + name + "'@"+it)
            if (search.size() > 0) search[0].uri
            else null
        }
    }

    Map searchPrefix(String name){
        //def search
        //def result = null
        _prefixes.findResult {key, value ->
            if(name.startsWith(key+':') || name.startsWith(value))
                [alias : key, 'uri': value]
            else null
        }
//        if(!result) {
//            /*
//            _prefixes.find{ key, value ->
//                println "<" + value + name + "> a ?class"
//                search = query("<" + value + name + "> rdf:type ?class")
//                if (search.size() > 0) {
//                    result = [alias: key, 'uri': value]
//                }
//                return true
//            }
//        */
//        }
//        return result
    }

    def getPrefixes(){
        def str = ''
        _prefixes.each {key, obj -> str += "PREFIX $key: <$obj>\n"}
        str
    }

//    def getPrefixesMap(){
//        return _prefixes
//    }

    def getBasePrefix(){
        return _prefixes['']
    }

    def setLang(String lg){
        lang = lg
    }

//    def getLang(){
//        return lang
//    }

    static isURI(Object id){
        if(id in String)
            return (id != null && id != '' && !id.contains(" ") && id.startsWith('http://'))
            //    return true
            //return false
        else if(id.class.isArray()){
            def isArray = true
            id.each{
                isArray = (it != null && it != '' && !it.contains(" ") && it.startsWith('http://')) ? isArray && true : isArray && false
            }
            return isArray
        }
        return false
    }

    /**
     * Methods taken fron Node where they didn't refer to the node, bringing a bad
     * modeling and made their use more confusing.
     * They may be separated in a subclass of Know.
     */


    /**
     * Used only in AdminController.groovy
     * @return
     */
    def selectSubject(String word){
        select('distinct ?s').query("?s ?p ?o. FILTER regex(str(?s), ':$word', 'i')")
    }

    def findByLabel(String word){
        select('distinct ?label').query("?s rdfs:label ?label. FILTER regex(str(?label), '$word', 'i')")
    }

    def findURI(String label){
        select('distinct ?uri').query("?uri rdfs:label '$label'@${lang}.")
    }

    /**
     * Used only in BootStrap.groovy
     * @return
     */
    def getUsers(){
        //def select = ''
        //def query = ''
        //def result

        query(  "?user a ui:User. "+
                "?user ui:hasUsername ?username. "+
                "?user ui:hasPassword ?password. ")

        //result =
        //query(query)
        //(result.size()==1)? result[0] : result
    }

    def deleteFeatures(String id){
        def uri = toURI('inds:'+id)
        delete("<$uri> dc:hasPart ?id. ?id ?p1 ?o. ?s ?p2 ?id")
    }

    def deleteAnalysis(String id){
        def uri = toURI('inds:'+id)
        delete("?s ?p1 <$uri>. <$uri> ?p2 ?o.")
    }

    def deleteBaseOntology(){
        delete("?s ?p ?o. filter(!STRSTARTS(STR(?s), 'http://semantic.icmc.usp.br/individuals#') || isBlank(?s) )", "{?s ?p ?o }")
    }

    /*
       Triples related.
     */

    private createTriples(String id, Map properties = [:]){
        String sparql = '<' + toURI(id) + '> '

        properties.each { key, property ->

//            String xsd = null
//            switch (property.dataType) {
//                case k.toURI('xsd:string'): xsd = "^^xsd:string; "; break
//                case k.toURI('xsd:double'): xsd = "^^xsd:double; "; break
//                case k.toURI('xsd:float'): xsd = "^^xsd:float; "; break
//                case k.toURI('owl:real'): xsd = "^^owl:real; "; break
//                case k.toURI('xsd:date'): xsd = "^^xsd:date; "; break
//                case k.toURI('xsd:time'): xsd = "^^xsd:time; "; break
//                case k.toURI('xsd:dateTime'): xsd = "^^xsd:dateTime; "; break
//                case k.toURI('xsd:duration'): xsd = '^^xsd:duration; '; break
//                case k.toURI('rdfs:Literal'): xsd = "^^rdfs:Literal; "; break
//            }
//            if (property.value in String[] || property.value in Object[]) {
//                if (xsd)
//                    property.value.each { sparql += "<${k.toURI(key)}> \"$it\"$xsd" }
//                else
//                    property.value.each {
//                        if (k.isURI(it))
//                            sparql += "<${k.toURI(key)}> <$it>; "
//                    }
//            } else {
//                if (xsd)
//                    sparql += "<${k.toURI(key)}> \"$property.value\"$xsd"
//                else {
//                    if (property.value in String && k.isURI(property.value))
//                        sparql += "<${k.toURI(key)}> <$property.value>; "
//                    else
//                    //println "Default: " + key + " : " + property.value
//                        sparql += "<${k.toURI(key)}> '$property.value'@$k.lang; "
//                }
//            }
//            //============
//            if (xsd) {
//                if (property.value in String[] || property.value in Object[])
//                    property.value.each { sparql += "<${k.toURI(key)}> \"$it\"$xsd" }
//                else sparql += "<${k.toURI(key)}> \"$property.value\"$xsd"
//            } else {
//                if (property.value in String[] || property.value in Object[])
//                    property.value.each {
//                        if (k.isURI(it))
//                            sparql += "<${k.toURI(key)}> <$it>; "
//                    }
//                else if (property.value in String && k.isURI(property.value))
//                    sparql += "<${k.toURI(key)}> <$property.value>; "
//                else
//                     //println "Default: " + key + " : " + property.value
//                    sparql += "<${k.toURI(key)}> '$property.value'@$k.lang; "
//            }


            switch (property.dataType) {
                case toURI('xsd:string'):
                    if(property.value in String[] || property.value in Object[])
                        property.value.each{ sparql += "<${toURI(key)}> \"$it\"^^xsd:string; " }
                    else sparql += "<${toURI(key)}> \"$property.value\"^^xsd:string; "
                    break
                case toURI('xsd:double'):
                    if(property.value in String[] || property.value in Object[])
                        property.value.each{ sparql += "<${toURI(key)}> \"" + it + "\"^^xsd:double; " }
                    else sparql += "<${toURI(key)}> \"" + property.value + "\"^^xsd:double; "
                    break
                case toURI('xsd:float'):
                    if(property.value in String[] || property.value in Object[])
                        property.value.each{ sparql += "<${toURI(key)}> \"" + it + "\"^^xsd:float; " }
                    else sparql += "<${toURI(key)}> \"" + property.value + "\"^^xsd:float; "
                    break
                case toURI('owl:real'):
                    if(property.value in String[] || property.value in Object[])
                        property.value.each{ sparql += "<${toURI(key)}> \"" + it + "\"^^owl:real; " }
                    else sparql += "<${toURI(key)}> \"" + property.value + "\"^^owl:real; "
                    break
                case toURI('xsd:date'):
                    if(property.value in String[] || property.value in Object[])
                        property.value.each{ sparql += "<${toURI(key)}> \"" + it + "\"^^xsd:date; " }
                    else sparql += "<${toURI(key)}> \"" + property.value + "\"^^xsd:date; "
                    break
                case toURI('xsd:time'):
                    if(property.value in String[] || property.value in Object[])
                        property.value.each{ sparql += "<${toURI(key)}> \"" + it + "\"^^xsd:time; " }
                    else sparql += "<${toURI(key)}> \"" + property.value + "\"^^xsd:time; "
                    break
                case toURI('xsd:dateTime'):
                    if(property.value in String[] || property.value in Object[])
                        property.value.each{ sparql += "<${toURI(key)}> \"" + it + "\"^^xsd:dateTime; " }
                    else sparql += "<${toURI(key)}> \"" + property.value + "\"^^xsd:dateTime; "
                    break
                case toURI('xsd:duration'):
                    if(property.value in String[] || property.value in Object[])
                        property.value.each{ sparql += "<${toURI(key)}> \"" + it + "\"^^xsd:duration; " }
                    else sparql += "<${toURI(key)}> \"" + property.value + "\"^^xsd:duration; "
                    break
                case toURI('rdfs:Literal'):
                    if(property.value in String[] || property.value in Object[])
                        property.value.each{ sparql += "<${toURI(key)}> '" + it + "'@" + lang + "; " }
                    else sparql += "<${toURI(key)}> '" + property.value + "'@" + lang + "; "
                    break
                default:
                    if(property.value in String[] || property.value in Object[])
                        property.value.each{ if(isURI(it)) {  sparql += "<${toURI(key)}> <" + it + ">; " } }
                    else if(property.value in String && isURI(property.value))
                        sparql += "<${toURI(key)}> <" + property.value + ">; "
                    else {
                        println "Default: " + key + " : " + property.value
                        sparql += "<${toURI(key)}> '" + property.value + "'@" + lang + "; "
                    }
            }
        }
        if(sparql.length()>2 && sparql.contains('; '))
            sparql = sparql[0..-3]+"."
        return sparql
    }

    def insertEvaluationObject(String id, Object type, Map properties = [:]){
        def evalObjId = toURI("inds:"+id)
        def name = toURI('ui:hasName')

        String sparql = "<" + evalObjId + "> "

        if(type.class.isArray()){
            type.each{
                sparql += "rdf:type <" + it + ">;"
            }
        }
        else if(type in String){
            sparql += "rdf:type <" + type + ">;"
        }

        sparql += "rdfs:label '" + properties[name].value + "'@pt;"+
                "rdfs:label '" + properties[name].value + "'@en. "

        sparql += createTriples(evalObjId, properties)

        /*sparql.split(';').each{
            println it
        }*/

        insert(sparql)
    }

    def insertAnalysis(String id, Map properties = [:]){
        def analysisId = toURI("inds:"+id)
        String sparql = "<$analysisId> rdf:type ui:Analysis. "

        //println properties

        sparql += createTriples(analysisId, properties)

        insert(sparql)
    }

    def insertFeatures(String id, Map individuals = [:]){
        def analysisId = toURI('inds:'+id)
        String sparql = ''
        String featureId = ''
        String indsBase = toURI('inds:')
        String domainBase = toURI(':')

        individuals.each{
            featureId = (it.key+'-'+id).replace(domainBase, indsBase)
            sparql += "<$featureId> rdf:type <$it.key>. "

            if(it.value.justification)
                sparql += "<$featureId> :hasJustification '$it.value.justification'. "

            if(isURI(it.value.value)){
                sparql += "<$featureId> ui:value <$it.value.value>. "
            }
            else{
                sparql += "<$featureId> ui:value _:$it.value.value.id. "
                sparql += "_:$it.value.value.id ui:dataValue '$it.value.value.dataValue'^^xsd:double. "
                sparql += "_:$it.value.value.id rdfs:label '$it.value.value.label'. "
            }

            if(it.value.weight){
                if(isURI(it.value.weight))
                    sparql += "<$featureId> ui:hasWeight <$it.value.weight>. "
                else {
                    sparql += "<$featureId> ui:hasWeight _:$it.value.weight.id. "
                    sparql += "_:$it.value.weight.id ui:dataValue '$it.value.weight.dataValue'^^xsd:double. "
                    sparql += "_:$it.value.weight.id rdfs:label '$it.value.weight.label'. "
                }
            }
            sparql += "<$featureId> dc:isPartOf <$analysisId>. "
            sparql += "<$analysisId> dc:hasPart <$featureId>. "
        }
        //println sparql
        insert(sparql)
    }

    def insertExtraFeatures(String id, Map individuals = [:]){
        def analysisId = toURI('inds:'+id)
        String sparql = ''
        String featureId = ''
        String indsBase = toURI('inds:')
        String domainBase = toURI(':')

        individuals.each{ individual ->
            individual.value.each{ list ->
                list.each{ item ->
                    featureId = (individual.key+'-'+item.key+'-'+id).replace(domainBase, indsBase)
                    sparql += "<$featureId> rdf:type <$individual.key>. "
                    sparql += "<$featureId> dc:isPartOf <$analysisId>. "
                    sparql += "<$analysisId> dc:hasPart <$featureId>. "
                    sparql += createTriples(featureId, item.value)
                }
            }
        }
        //println sparql
        insert(sparql)
    }

    def insertUser(String id, Map properties = [:]){
        def userId = toURI('inds:'+id)
        String sparql = "<$userId> rdf:type ui:User. "

        sparql += createTriples(userId, properties)

        //println sparql

        insert(sparql)
    }

    def insertTriples(String id, Map properties = [:]){
        insert(createTriples(id, properties))
    }
}
