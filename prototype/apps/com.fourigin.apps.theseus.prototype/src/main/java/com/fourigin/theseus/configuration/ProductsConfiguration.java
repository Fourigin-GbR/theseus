package com.fourigin.theseus.configuration;

import com.fourigin.theseus.core.ProductType;
import com.fourigin.theseus.core.builder.EditablePropertyDefinitionBuilder;
import com.fourigin.theseus.core.builder.NoValuePropertyDefinitionBuilder;
import com.fourigin.theseus.core.builder.ProductBuilder;
import com.fourigin.theseus.core.builder.SetPropertyDefinitionBuilder;
import com.fourigin.theseus.core.types.EditablePropertyType;
import com.fourigin.theseus.core.types.NoValuePropertyType;
import com.fourigin.theseus.core.types.SetPropertyType;
import com.fourigin.theseus.repository.ProductRepositoryStub;
import com.fourigin.theseus.repository.PropertyRepositoryStub;
import com.fourigin.theseus.service.DefaultProductService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.HashSet;

@Configuration
public class ProductsConfiguration {

    @Bean
    public ProductRepositoryStub productRepositoryStub() {
        ProductRepositoryStub stub = new ProductRepositoryStub();

        ProductBuilder builder = new ProductBuilder();

        stub.createProduct(builder
            .reset()
            .code("standard-clock-template")
            .name("en", "Standard clock template")
            .name("ru", "Стандартный шаблон часов")
            .productType(ProductType.MASTER)
            .build()
        );
        stub.createProduct(builder
            .reset()
            .code("forte-normal")
            .referenceCode("F30")
            .name("en", "Forte")
            .name("ru", "Форте")
            .productType(ProductType.PRODUCT)
            .classification("premium")
            .priceValue("EUR", 359.0d)
            .priceValue("RUB", 12000.0d)
            .build()
        );
        stub.createProduct(builder
            .reset()
            .code("mira")
            .name("en", "Mira")
            .name("ru", "Мира")
            .productType(ProductType.PRODUCT)
            .classification("premium")
            .priceValue("EUR", 369.0d)
            .priceValue("RUB", 13000.0d)
            .build()
        );
        stub.createProduct(builder
            .reset()
            .code("tessoro")
            .name("en", "Tessoro")
            .name("ru", "Тессоро")
            .productType(ProductType.PRODUCT)
            .classification("premium")
            .priceValue("EUR", 379.0d)
            .priceValue("RUB", 14000.0d)
            .build()
        );
        stub.createProduct(builder
            .reset()
            .code("eva-normal")
            .name("en", "Eva")
            .name("ru", "Ева")
            .productType(ProductType.PRODUCT)
            .classification("premium")
            .priceValue("EUR", 389.0d)
            .priceValue("RUB", 15000.0d)
            .build()
        );
        stub.createProduct(builder
            .reset()
            .code("eva-round")
            .name("en", "Eva Round")
            .name("ru", "Ева Раунд")
            .productType(ProductType.PRODUCT)
            .classification("premium")
            .priceValue("EUR", 399.0d)
            .priceValue("RUB", 16000.0d)
            .build()
        );
        stub.createProduct(builder
            .reset()
            .code("eva-mini")
            .name("en", "Eva Mini")
            .name("ru", "Ева Мини")
            .productType(ProductType.PRODUCT)
            .classification("premium")
            .priceValue("EUR", 409.0d)
            .priceValue("RUB", 17000.0d)
            .build()
        );
        stub.createProduct(builder
            .reset()
            .code("aurora")
            .name("en", "Aurora")
            .name("ru", "Аврора")
            .productType(ProductType.PRODUCT)
            .classification("premium")
            .priceValue("EUR", 419.0d)
            .priceValue("RUB", 18000.0d)
            .build()
        );
        stub.createProduct(builder
            .reset()
            .code("forte-mini")
            .referenceCode("F31")
            .name("en", "Forte Mini")
            .name("ru", "Форте Мини")
            .description("en", "We are often asked why we make clocks. We see it as our chance to make this world a little more beautiful by letting people surround themselves with beautiful things. Our Forte Mini M Steel & Wood in wenge, one of the signature models for our team, is a part of the answer.")
            .productType(ProductType.PRODUCT)
            .classification("premium")
            .priceValue("EUR", 429.0d)
            .priceValue("RUB", 19000.0d)
            .build()
        );
        stub.createProduct(builder
            .reset()
            .code("aura")
            .name("en", "Aura")
            .name("ru", "Аура")
            .productType(ProductType.PRODUCT)
            .classification("premium")
            .priceValue("EUR", 439.0d)
            .priceValue("RUB", 20000.0d)
            .build()
        );
        stub.createProduct(builder
            .reset()
            .code("aria")
            .referenceCode("A70")
            .name("en", "Aria")
            .name("ru", "Ариа")
            .productType(ProductType.PRODUCT)
            .classification("premium")
            .priceValue("EUR", 449.0d)
            .priceValue("RUB", 21000.0d)
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

        stub.createPropertyDefinition(
            noValueDefinitionBuilder
                .reset()
                .type(warranty)
                .propertyCode("warranty")
                .propertyName("en", "Manufacturer warranty")
                .propertyName("ru", "Гарантия производителя")
                .build()
        );
        stub.createPropertyDefinition(
            editableDefinitionBuilder
                .reset()
                .propertyCode("dimension/box-mechanism-diameter")
                .propertyName("en", "Box mechanism diameter")
                .propertyName("ru", "Диаметр часового механизма")
                .type(millimeters)
                .build()
        );
        stub.createPropertyDefinition(
            editableDefinitionBuilder
                .reset()
                .propertyCode("dimension/hour-hand-length")
                .propertyName("en", "Hour hand length")
                .propertyName("ru", "Длина часовой стрелки")
                .type(millimeters)
                .build()
        );
        stub.createPropertyDefinition(
            editableDefinitionBuilder
                .reset()
                .propertyCode("dimension/minute-hand-length")
                .propertyName("en", "Minute hand length")
                .propertyName("ru", "Длина минутной стрелки")
                .type(millimeters)
                .build()
        );
        stub.createPropertyDefinition(
            setDefinitionBuilder
                .reset()
                .propertyCode("material/box-mechanism")
                .propertyName("en", "Box mechanism material")
                .propertyName("ru", "Материал часового механизма")
                .type(boxMechanism)
                .build()
        );
        stub.createPropertyDefinition(
            setDefinitionBuilder
                .reset()
                .propertyCode("material/hands")
                .propertyName("en", "Hands material")
                .propertyName("ru", "Материал стрелок часов")
                .type(hands)
                .build()
        );

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