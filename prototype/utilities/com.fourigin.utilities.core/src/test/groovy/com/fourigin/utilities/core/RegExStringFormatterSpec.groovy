package com.fourigin.utilities.core

import spock.lang.Specification
import spock.lang.Unroll

class RegExStringFormatterSpec extends Specification {

    @Unroll
    'format(#data) returns #expectedResult'() {
        when:
        RegExStringFormatter formatter = new RegExStringFormatter()
        formatter.setSelectionPattern('^(DE[0-9]{2})\\s?([0-9]{4})\\s?([0-9]{4})\\s?([0-9]{4})\\s?([0-9]{4})\\s?([0-9]{2})\\s?$')
        formatter.setReplacingPattern('$1 $2 $3 $4 $5 $6')

        then:
        formatter.format(data) == expectedResult

        where:
        data                                    | expectedResult
        'DE89 3704 0044 0532 0130 00'           | 'DE89 3704 0044 0532 0130 00'
        'DE89370400440532013000'                | 'DE89 3704 0044 0532 0130 00'
        'DE00123456789012345678'                | 'DE00 1234 5678 9012 3456 78'
    }

//    @Unroll
//    'parse(#formattedData) returns #expectedResult'() {
//        when:
//        RegExStringFormatter formatter = new RegExStringFormatter()
//        formatter.setFromStringSelection('^(DE[0-9]{2})\\s?([0-9]{4})\\s?([0-9]{4})\\s?([0-9]{4})\\s?([0-9]{4})\\s?([0-9]{2})\\s?$')
//        formatter.setFromStringReplacing('$1$2$3$4$5$6')
//
//        then:
//        formatter.parse(formattedData) == expectedResult
//
//        where:
//        formattedData                                    | expectedResult
//        'DE89 3704 0044 0532 0130 00'           | 'DE89370400440532013000'
//        'DE89370400440532013000'                | 'DE89370400440532013000'
//        'DE00123456789012345678'                | 'DE00123456789012345678'
//    }
}
