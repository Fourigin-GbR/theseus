package com.fourigin.theseus.core.builder;

import com.fourigin.theseus.core.PropertyDefinition;
import com.fourigin.theseus.core.Translation;
import com.fourigin.theseus.core.types.EditablePropertyType;

public class EditablePropertyDefinitionBuilder {
    private String propertyCode;
    private Translation propertyName;
    private EditablePropertyType propertyType;

//    private String typeName;
//    private String typeDescription;
//    private String typePattern;


    
    public EditablePropertyDefinitionBuilder withPropertyCode(String code){
        this.propertyCode = code;
        return this;
    }

    public EditablePropertyDefinitionBuilder withPropertyName(String language, String name){
        this.propertyName.setTranslation(language, name);
        return this;
    }

    public EditablePropertyDefinitionBuilder withType(EditablePropertyType type){
        this.propertyType = type;
        return this;
    }

//    public EditablePropertyDefinitionBuilder withTypeName(String typeName){
//        this.typeName = typeName;
//        return this;
//    }
//
//    public EditablePropertyDefinitionBuilder withTypeDescription(String typeDescription){
//        this.typeDescription = typeDescription;
//        return this;
//    }
//
//    public EditablePropertyDefinitionBuilder withTypePattern(String typePattern){
//        this.typePattern = typePattern;
//        return this;
//    }

    public EditablePropertyDefinitionBuilder reset(){
        this.propertyCode = null;
        this.propertyName = new Translation();
//        this.typeName = null;
//        this.typeDescription = null;
//        this.typePattern = null;

        return this;
    }

    public PropertyDefinition<EditablePropertyType> build(){
        PropertyDefinition<EditablePropertyType> definition = new PropertyDefinition<>();

        definition.setCode(propertyCode);
        definition.setName(propertyName);
        definition.setPropertyType(propertyType);

        return definition;
    }
}
