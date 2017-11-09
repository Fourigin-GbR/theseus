package com.fourigin.theseus.core.builder;

import com.fourigin.theseus.core.PropertyDefinition;
import com.fourigin.theseus.core.Translation;
import com.fourigin.theseus.core.types.NoValuePropertyType;

public class NoValuePropertyDefinitionBuilder {
    private String propertyCode;
    private Translation propertyName;
    private NoValuePropertyType propertyType;

    public NoValuePropertyDefinitionBuilder propertyCode(String code){
        this.propertyCode = code;
        return this;
    }

    public NoValuePropertyDefinitionBuilder propertyName(String language, String name){
        this.propertyName.setTranslation(language, name);
        return this;
    }

    public NoValuePropertyDefinitionBuilder type(NoValuePropertyType propertyType){
        this.propertyType = propertyType;
        return this;
    }

    public NoValuePropertyDefinitionBuilder reset(){
        this.propertyCode = null;
        this.propertyName = new Translation();
        this.propertyType = null;

        return this;
    }

    public PropertyDefinition<NoValuePropertyType> build(){
        PropertyDefinition<NoValuePropertyType> definition = new PropertyDefinition<>();

        definition.setCode(propertyCode);
        definition.setName(propertyName);
        definition.setPropertyType(propertyType);

        return definition;
    }
}
