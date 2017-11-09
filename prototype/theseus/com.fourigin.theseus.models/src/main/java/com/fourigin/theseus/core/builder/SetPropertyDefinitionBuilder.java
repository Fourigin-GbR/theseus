package com.fourigin.theseus.core.builder;

import com.fourigin.theseus.core.PropertyDefinition;
import com.fourigin.theseus.core.Translation;
import com.fourigin.theseus.core.types.SetPropertyType;

public class SetPropertyDefinitionBuilder {
    private String propertyCode;
    private Translation propertyName;
    private SetPropertyType propertyType;

    public SetPropertyDefinitionBuilder propertyCode(String code){
        this.propertyCode = code;
        return this;
    }

    public SetPropertyDefinitionBuilder propertyName(String language, String name){
        this.propertyName.setTranslation(language, name);
        return this;
    }

    public SetPropertyDefinitionBuilder type(SetPropertyType type){
        this.propertyType = type;
        return this;
    }

    public SetPropertyDefinitionBuilder reset(){
        this.propertyCode = null;
        this.propertyName = new Translation();
        this.propertyType = null;

        return this;
    }

    public PropertyDefinition<SetPropertyType> build(){
        PropertyDefinition<SetPropertyType> definition = new PropertyDefinition<>();

        definition.setCode(propertyCode);
        definition.setName(propertyName);
        definition.setPropertyType(propertyType);

        return definition;
    }
}
