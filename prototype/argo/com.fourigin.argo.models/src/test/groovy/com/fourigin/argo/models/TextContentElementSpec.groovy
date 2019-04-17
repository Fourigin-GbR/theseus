package com.fourigin.argo.models

import com.fourigin.argo.models.content.elements.LanguageContent
import com.fourigin.argo.models.content.elements.TextContentElement
import de.huxhorn.sulky.junit.JUnitTools
import spock.lang.Specification

class TextContentElementSpec extends Specification {
//	@Unroll
//	def 'fromString("#input") works as expected'() {
//		when:
//		def country = Country.fromString(input)
//
//		then:
//		country != null
//		country.code == input
//		country.toString() == input
//
//		when:
//		def country2 = Country.fromString(input)
//
//		then:
//		country.hashCode() == country2.hashCode()
//		country == country2
//		country.is(country2)
//
//		where:
//		input << ['EM', 'DE', 'US']
//	}
//
//	def 'fromString(null) fails as expected'() {
//		when:
//		Country.fromString(null)
//
//		then:
//		NullPointerException ex = thrown()
//		ex.message == 'countryCode must not be null!'
//	}
//
//	@Unroll
//	def 'fromString("#input") fails as expected'() {
//		when:
//		Country.fromString(input)
//
//		then:
//		IllegalArgumentException ex = thrown()
//		ex.message == 'countryCode must match pattern '+Country.REGEX_PATTERN_STRING+'!'
//
//		where:
//		input << ['', 'em', 'DEE', '12']
//	}
//
    def "setContent work as expected."() {
        given:
        def instance = new TextContentElement()
        instance.setContent(LanguageContent.forContent('en', 'a content'))

        expect:
        instance.content['en'] == 'a content'
    }

    def "setContextSpecificContent work as expected."() {
        given:
        TextContentElement instance = new TextContentElement()

        when:
        instance.setContent(LanguageContent.forContent('en', 'a content', "ctx-1", null))

        then:
        instance.content['ctx-1'] == null

        when:
		instance.setContent(LanguageContent.forContent('en', 'a content', "ctx-1", 'a specific content for ctx-1'))

        then:
        instance.content['en'] == 'a content'
        instance.content['ctx-1'] == 'a specific content for ctx-1'
    }
    
	def "equals(), hashCode() and toString() work as expected."() {
		given:
		def value = new TextContentElement.Builder()
                .withName('name')
                .withContent('en','content')
                .withAttribute('attr1', 'value1')
                .build()
		def sameValue = new TextContentElement.Builder()
                .withName('name')
                .withContent('en','content')
                .withAttribute('attr1', 'value1')
                .build()
		def differentValue = new TextContentElement.Builder()
                .withName('name')
                .withContent('en','other content')
                .withAttribute('attr2', 'value2')
                .build()

		expect:
		value == value
		value == sameValue
		value.hashCode() == sameValue.hashCode()
		value.toString() == sameValue.toString()
		!value.is(sameValue)
        
		and:
		value != differentValue
	}

	def 'withAttribute() work as expected'() {
		when:
		def element = new TextContentElement.Builder()
				.withName('name')
				.withAttribute('a-name', 'a-value')
				.build()

		then:
		element.attributes['a-name'] == 'a-value'

		when:
		element = new TextContentElement.Builder()
				.withName('name')
				.withAttribute('a-name', 'a-value')
				.withAttribute('b-name', 'b-value')
				.withAttribute('a-name', null)
				.build()

		then:
		element.attributes.size() == 1

		when:
		element  = new TextContentElement.Builder()
				.withName('name')
				.withAttribute(null, 'a-value')
				.build()

		then:
		element.attributes == null
	}

	def 'serialization returns the same value'() {
		when:
		def value = new TextContentElement.Builder()
				.withName('name')
				.withTitle('en','title')
				.withContent('en','content')
				.withContent('de', 'de specific content')
				.build()

		then:
		JUnitTools.testSerialization(value, false)
	}
}
