package com.fourigin.theseus.configuration;

import com.fourigin.theseus.core.Price;
import com.fourigin.theseus.core.ProductType;
import com.fourigin.theseus.core.Property;
import com.fourigin.theseus.core.PropertyAvailability;
import com.fourigin.theseus.core.PropertyDefinition;
import com.fourigin.theseus.core.builder.EditablePropertyDefinitionBuilder;
import com.fourigin.theseus.core.builder.NoValuePropertyDefinitionBuilder;
import com.fourigin.theseus.core.builder.PriceMapBuilder;
import com.fourigin.theseus.core.builder.ProductBuilder;
import com.fourigin.theseus.core.builder.SetPropertyDefinitionBuilder;
import com.fourigin.theseus.core.types.AvailablePropertyValue;
import com.fourigin.theseus.core.types.EditablePropertyType;
import com.fourigin.theseus.core.types.EditablePropertyValue;
import com.fourigin.theseus.core.types.NoValuePropertyType;
import com.fourigin.theseus.core.types.SetPropertyType;
import com.fourigin.theseus.core.types.SetPropertyValue;
import com.fourigin.theseus.core.types.SetPropertyValueEntry;
import com.fourigin.theseus.repository.ProductRepositoryStub;
import com.fourigin.theseus.repository.PropertyRepositoryStub;
import com.fourigin.theseus.service.DefaultProductService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

@Configuration
public class ProductsConfiguration {

    @Bean
    public ProductRepositoryStub productRepositoryStub() {
        ProductRepositoryStub stub = new ProductRepositoryStub();

        ProductBuilder builder = new ProductBuilder();

        stub.createProduct(builder
            .reset()
            .withCode("standard-clock-template")
            .withName("en", "Standard clock template")
            .withName("ru", "Стандартный шаблон часов")
            .withProductType(ProductType.MASTER)
            .build()
        );
        stub.createProduct(builder
            .reset()
            .withCode("forte-normal")
            .withReferenceCode("F30")
            .withName("en", "Forte")
            .withName("ru", "Форте")
            .withProductType(ProductType.PRODUCT)
            .withClassification("premium")
            .withPriceValue("EUR", 359.0d)
            .withPriceValue("RUB", 12000.0d)
            .build()
        );
        stub.createProduct(builder
            .reset()
            .withCode("mira")
            .withName("en", "Mira")
            .withName("ru", "Мира")
            .withProductType(ProductType.PRODUCT)
            .withClassification("premium")
            .withPriceValue("EUR", 369.0d)
            .withPriceValue("RUB", 13000.0d)
            .build()
        );
        stub.createProduct(builder
            .reset()
            .withCode("tessoro")
            .withName("en", "Tessoro")
            .withName("ru", "Тессоро")
            .withProductType(ProductType.PRODUCT)
            .withClassification("premium")
            .withPriceValue("EUR", 379.0d)
            .withPriceValue("RUB", 14000.0d)
            .build()
        );
        stub.createProduct(builder
            .reset()
            .withCode("eva-normal")
            .withName("en", "Eva")
            .withName("ru", "Ева")
            .withProductType(ProductType.PRODUCT)
            .withClassification("premium")
            .withPriceValue("EUR", 389.0d)
            .withPriceValue("RUB", 15000.0d)
            .build()
        );
        stub.createProduct(builder
            .reset()
            .withCode("eva-round")
            .withName("en", "Eva Round")
            .withName("ru", "Ева Раунд")
            .withProductType(ProductType.PRODUCT)
            .withClassification("premium")
            .withPriceValue("EUR", 399.0d)
            .withPriceValue("RUB", 16000.0d)
            .build()
        );
        stub.createProduct(builder
            .reset()
            .withCode("eva-mini")
            .withName("en", "Eva Mini")
            .withName("ru", "Ева Мини")
            .withProductType(ProductType.PRODUCT)
            .withClassification("premium")
            .withPriceValue("EUR", 409.0d)
            .withPriceValue("RUB", 17000.0d)
            .build()
        );
        stub.createProduct(builder
            .reset()
            .withCode("aurora")
            .withName("en", "Aurora")
            .withName("ru", "Аврора")
            .withProductType(ProductType.PRODUCT)
            .withClassification("premium")
            .withPriceValue("EUR", 419.0d)
            .withPriceValue("RUB", 18000.0d)
            .build()
        );
        stub.createProduct(builder
            .reset()
            .withCode("forte-mini")
            .withReferenceCode("F31")
            .withName("en", "Forte Mini")
            .withName("ru", "Форте Мини")
            .withDescription("en", "We are often asked why we make clocks. We see it as our chance to make this world a little more beautiful by letting people surround themselves with beautiful things. Our Forte Mini M Steel & Wood in wenge, one of the signature models for our team, is a part of the answer.")
            .withProductType(ProductType.PRODUCT)
            .withClassification("premium")
            .withPriceValue("EUR", 429.0d)
            .withPriceValue("RUB", 19000.0d)
            .build()
        );
        stub.createProduct(builder
            .reset()
            .withCode("aura")
            .withName("en", "Aura")
            .withName("ru", "Аура")
            .withProductType(ProductType.PRODUCT)
            .withClassification("premium")
            .withPriceValue("EUR", 439.0d)
            .withPriceValue("RUB", 20000.0d)
            .build()
        );
        stub.createProduct(builder
            .reset()
            .withCode("aria")
            .withReferenceCode("A70")
            .withName("en", "Aria")
            .withName("ru", "Ариа")
            .withProductType(ProductType.PRODUCT)
            .withClassification("premium")
            .withPriceValue("EUR", 449.0d)
            .withPriceValue("RUB", 21000.0d)
            .build()
        );

        return stub;
    }

    @Bean
    public PropertyRepositoryStub propertyRepositoryStub() {
        PropertyRepositoryStub stub = new PropertyRepositoryStub();

        NoValuePropertyType warranty = new NoValuePropertyType();
        warranty.setName("warranty");
        warranty.setDescription("Manufacturer warranty");

        EditablePropertyType millimeters = new EditablePropertyType();
        millimeters.setName("mm");
        millimeters.setDescription("Millimeters");
        millimeters.setPattern("\\d+");

        SetPropertyType boxMechanism = new SetPropertyType();
        boxMechanism.setName("box-mechanism");
        boxMechanism.setDescription("Box mechanism");
        boxMechanism.setKeys(new HashSet<>(Arrays.asList("209", "304")));

        SetPropertyType hands = new SetPropertyType();
        hands.setName("hands");
        hands.setDescription("Hands");
        hands.setKeys(new HashSet<>(Arrays.asList("H317", "H421", "H527", "H619", "H703")));

        NoValuePropertyDefinitionBuilder noValueDefinitionBuilder = new NoValuePropertyDefinitionBuilder();
        EditablePropertyDefinitionBuilder editableDefinitionBuilder = new EditablePropertyDefinitionBuilder();
        SetPropertyDefinitionBuilder setDefinitionBuilder = new SetPropertyDefinitionBuilder();

        String warrantyCode = "warranty";
        String boxMechanismDiameterCode = "dimension/box-mechanism-diameter";
        String hourHandLengthCode = "dimension/hour-hand-length";
        String minuteHandLengthCode = "dimension/minute-hand-length";
        String boxMechanismCode = "material/box-mechanism";
        String handsCode = "material/hands";

        // definitions
        PropertyDefinition<NoValuePropertyType> warrantyDefinition = noValueDefinitionBuilder
            .reset()
            .withType(warranty)
            .withPropertyCode(warrantyCode)
            .withPropertyName("en", "Manufacturer warranty")
            .withPropertyName("ru", "Гарантия производителя")
            .build();

        PropertyDefinition<EditablePropertyType> boxMechanismDiameterDefinition = editableDefinitionBuilder
            .reset()
            .withPropertyCode(boxMechanismDiameterCode)
            .withPropertyName("en", "Box mechanism diameter")
            .withPropertyName("ru", "Диаметр часового механизма")
            .withType(millimeters)
            .build();

        PropertyDefinition<EditablePropertyType> hourHandLengthDefinition = editableDefinitionBuilder
            .reset()
            .withPropertyCode(hourHandLengthCode)
            .withPropertyName("en", "Hour hand length")
            .withPropertyName("ru", "Длина часовой стрелки")
            .withType(millimeters)
            .build();

        PropertyDefinition<EditablePropertyType> minuteHandLengthDefinition = editableDefinitionBuilder
            .reset()
            .withPropertyCode(minuteHandLengthCode)
            .withPropertyName("en", "Minute hand length")
            .withPropertyName("ru", "Длина минутной стрелки")
            .withType(millimeters)
            .build();

        PropertyDefinition<SetPropertyType> boxMechanismDefinition = setDefinitionBuilder
            .reset()
            .withPropertyCode(boxMechanismCode)
            .withPropertyName("en", "Box mechanism material")
            .withPropertyName("ru", "Материал часового механизма")
            .withType(boxMechanism)
            .build();

        PropertyDefinition<SetPropertyType> handsDefinition = setDefinitionBuilder
            .reset()
            .withPropertyCode(handsCode)
            .withPropertyName("en", "Hands material")
            .withPropertyName("ru", "Материал стрелок часов")
            .withType(hands)
            .build();

        stub.createPropertyDefinition(warrantyDefinition);
        stub.createPropertyDefinition(boxMechanismDiameterDefinition);
        stub.createPropertyDefinition(hourHandLengthDefinition);
        stub.createPropertyDefinition(minuteHandLengthDefinition);
        stub.createPropertyDefinition(boxMechanismDefinition);
        stub.createPropertyDefinition(handsDefinition);

        // values

        {
            // -- aria
            stub.createProductProperty("aria", new Property<>(
                warrantyCode, warrantyDefinition, new AvailablePropertyValue(PropertyAvailability.STANDARD)
            ));
            stub.createProductProperty("aria", new Property<>(
                boxMechanismDiameterCode, boxMechanismDiameterDefinition, new EditablePropertyValue<>(PropertyAvailability.STANDARD, 180L)
            ));
            stub.createProductProperty("aria", new Property<>(
                hourHandLengthCode, hourHandLengthDefinition, new EditablePropertyValue<>(PropertyAvailability.STANDARD, 300L)
            ));
            stub.createProductProperty("aria", new Property<>(
                minuteHandLengthCode, minuteHandLengthDefinition, new EditablePropertyValue<>(PropertyAvailability.STANDARD, 420L)
            ));

            Map<String, SetPropertyValueEntry> boxMechanismValues = new HashMap<>();
            boxMechanismValues.put("209", new SetPropertyValueEntry(PropertyAvailability.STANDARD));
            boxMechanismValues.put("304", new SetPropertyValueEntry(PropertyAvailability.ON_REQUEST));

            stub.createProductProperty("aria", new Property<>(
                boxMechanismCode, boxMechanismDefinition, new SetPropertyValue(boxMechanism, boxMechanismValues)
            ));

            PriceMapBuilder priceMapBuilder = new PriceMapBuilder();

            Map<String, SetPropertyValueEntry> handsValues = new HashMap<>();
            handsValues.put("H317", new SetPropertyValueEntry(
                PropertyAvailability.OPTIONAL,
                new Price("EUR", priceMapBuilder
                    .clear()
                    .put("EUR", 120d)
                    .put("RUB", 20000d)
                    .build()
                )
            ));
            handsValues.put("H421", new SetPropertyValueEntry(
                PropertyAvailability.OPTIONAL,
                new Price("EUR", priceMapBuilder
                    .clear()
                    .put("EUR", 120d)
                    .put("RUB", 20000d)
                    .build()
                )
            ));
            handsValues.put("H527", new SetPropertyValueEntry(
                PropertyAvailability.OPTIONAL,
                new Price("EUR", priceMapBuilder
                    .clear()
                    .put("EUR", 160d)
                    .put("RUB", 22000d)
                    .build()
                )
            ));
            handsValues.put("H619", new SetPropertyValueEntry(
                PropertyAvailability.OPTIONAL,
                new Price("EUR", priceMapBuilder
                    .clear()
                    .put("EUR", 160d)
                    .put("RUB", 22000d)
                    .build()
                )
            ));
            handsValues.put("H703", new SetPropertyValueEntry(
                PropertyAvailability.ON_REQUEST
            ));

            stub.createProductProperty("aria", new Property<>(
                handsCode, handsDefinition, new SetPropertyValue(hands, handsValues)
            ));
        }

        {
            // -- aurora
            stub.createProductProperty("aurora", new Property<>(
                warrantyCode, warrantyDefinition, new AvailablePropertyValue(PropertyAvailability.STANDARD)
            ));
            stub.createProductProperty("aurora", new Property<>(
                boxMechanismDiameterCode, boxMechanismDiameterDefinition, new EditablePropertyValue<>(PropertyAvailability.STANDARD, 300L)
            ));

            Map<String, SetPropertyValueEntry> boxMechanismValues = new HashMap<>();
            boxMechanismValues.put("209", new SetPropertyValueEntry(PropertyAvailability.STANDARD));

            stub.createProductProperty("aurora", new Property<>(
                boxMechanismCode, boxMechanismDefinition, new SetPropertyValue(boxMechanism, boxMechanismValues)
            ));
        }

        return stub;
    }

    @Bean
    public DefaultProductService defaultProductService() {
        DefaultProductService service = new DefaultProductService();

        service.setProductRepository(productRepositoryStub());
        service.setPropertyRepository(propertyRepositoryStub());

        return service;
    }
}
