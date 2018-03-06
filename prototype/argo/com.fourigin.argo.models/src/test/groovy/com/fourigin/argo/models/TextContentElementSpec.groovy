package com.fourigin.argo.models

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
        instance.setContent('a content')

        expect:
        instance.content == 'a content'
    }

    def "setContextSpecificContent work as expected."() {
        given:
        TextContentElement instance = new TextContentElement()

        when:
        instance.setContent('a content')
        instance.setContextSpecificContent('ctx-1', null)

        then:
        instance.contextSpecificContent == null

        when:
        instance.setContent('a content')
        instance.setContextSpecificContent('ctx-1', 'a specific content for ctx-1')

        then:
        instance.content == 'a content'
        instance.contextSpecificContent != null
        !instance.contextSpecificContent.isEmpty()
        instance.getContextSpecificContent('ctx-1', true) == 'a specific content for ctx-1'
    }

    def "getContextSpecificContent work as expected."() {
        given:
        def instance = new TextContentElement()
        instance.setContent('a content')

        when:
        instance.setContextSpecificContent('ctx-1', 'a specific content for ctx-1')

        then:
        instance.content == 'a content'
        instance.getContextSpecificContent('ctx-1', true) == 'a specific content for ctx-1'
        instance.getContextSpecificContent('ctx-1', false) == 'a specific content for ctx-1'
        instance.getContextSpecificContent('ctx-2', true) == 'a content'
        instance.getContextSpecificContent('ctx-2', false) == null

        when:
        instance.setContextSpecificContent('ctx-1', null)

        then:
        instance.content == 'a content'
        instance.getContextSpecificContent('ctx-1', true) == 'a content'
        instance.getContextSpecificContent('ctx-1', false) == null
        instance.getContextSpecificContent('ctx-2', true) == 'a content'
        instance.getContextSpecificContent('ctx-2', false) == null

        when:
        instance.setContextSpecificContent(null)

        then:
        instance.content == 'a content'
        instance.getContextSpecificContent('ctx-1', true) == 'a content'
        instance.getContextSpecificContent('ctx-1', false) == null
        instance.getContextSpecificContent('ctx-2', true) == 'a content'
        instance.getContextSpecificContent('ctx-2', false) == null
    }

	def "equals(), hashCode() and toString() work as expected."() {
		given:
		def value = new TextContentElement.Builder()
                .withName('name')
                .withContent('content')
                .withAttribute('attr1', 'value1')
                .build()
		def sameValue = new TextContentElement.Builder()
                .withName('name')
                .withContent('content')
                .withAttribute('attr1', 'value1')
                .build()
		def differentValue = new TextContentElement.Builder()
                .withName('name')
                .withContent('other content')
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
				.withTitle('title')
				.withContent('content')
				.withContextSpecificContent('DE', 'de specific content')
				.build()

		then:
		JUnitTools.testSerialization(value, false)
	}
}
