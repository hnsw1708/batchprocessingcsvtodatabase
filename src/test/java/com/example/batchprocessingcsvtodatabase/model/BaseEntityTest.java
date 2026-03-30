package com.example.batchprocessingcsvtodatabase.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BaseEntity Unit Tests")
class BaseEntityTest {

    /**
     * Concrete subclass used for testing since BaseEntity is abstract-like (MappedSuperclass).
     */
    static class ConcreteEntity extends BaseEntity {
        // No additional fields needed for basic tests
    }

    // -------------------------------------------------------------------------
    // Instantiation
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should instantiate ConcreteEntity without throwing")
    void shouldInstantiateWithoutThrowing() {
        assertDoesNotThrow(ConcreteEntity::new);
    }

    @Test
    @DisplayName("getId() should return null when id has not been set (no persistence context)")
    void getIdShouldReturnNullByDefault() {
        var entity = new ConcreteEntity();
        assertNull(entity.getId(),
                "id should be null before persistence assigns a value");
    }

    // -------------------------------------------------------------------------
    // Reflection-based id injection (simulates what JPA would do at runtime)
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("getId() should return the injected id value")
    void getIdShouldReturnInjectedValue() throws Exception {
        var entity = new ConcreteEntity();
        setId(entity, 42L);
        assertEquals(42L, entity.getId());
    }

    @Test
    @DisplayName("getId() should return 1 as minimum valid id")
    void getIdShouldReturnMinimumValidId() throws Exception {
        var entity = new ConcreteEntity();
        setId(entity, 1L);
        assertEquals(1L, entity.getId());
    }

    @Test
    @DisplayName("getId() should handle Long.MAX_VALUE")
    void getIdShouldHandleLargeIdValue() throws Exception {
        var entity = new ConcreteEntity();
        setId(entity, Long.MAX_VALUE);
        assertEquals(Long.MAX_VALUE, entity.getId());
    }

    @Test
    @DisplayName("Two distinct instances should have independent id values")
    void twoInstancesShouldHaveIndependentIds() throws Exception {
        var entity1 = new ConcreteEntity();
        var entity2 = new ConcreteEntity();

        setId(entity1, 10L);
        setId(entity2, 20L);

        assertAll(
                () -> assertEquals(10L, entity1.getId()),
                () -> assertEquals(20L, entity2.getId()),
                () -> assertNotEquals(entity1.getId(), entity2.getId())
        );
    }

    // -------------------------------------------------------------------------
    // Annotation presence checks
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("BaseEntity class should be annotated with @MappedSuperclass")
    void baseEntityShouldBeAnnotatedWithMappedSuperclass() {
        assertTrue(
                BaseEntity.class.isAnnotationPresent(MappedSuperclass.class),
                "@MappedSuperclass annotation must be present on BaseEntity"
        );
    }

    @Test
    @DisplayName("'id' field should be annotated with @Id")
    void idFieldShouldBeAnnotatedWithId() throws NoSuchFieldException {
        var idField = BaseEntity.class.getDeclaredField("id");
        assertTrue(
                idField.isAnnotationPresent(Id.class),
                "'id' field must carry @Id annotation"
        );
    }

    @Test
    @DisplayName("'id' field should be annotated with @GeneratedValue(strategy = IDENTITY)")
    void idFieldShouldHaveGeneratedValueWithIdentityStrategy() throws NoSuchFieldException {
        var idField = BaseEntity.class.getDeclaredField("id");
        assertTrue(
                idField.isAnnotationPresent(GeneratedValue.class),
                "'id' field must carry @GeneratedValue annotation"
        );

        var generatedValue = idField.getAnnotation(GeneratedValue.class);
        assertEquals(
                GenerationType.IDENTITY,
                generatedValue.strategy(),
                "GenerationType must be IDENTITY"
        );
    }

    @Test
    @DisplayName("'id' field should be of type Long")
    void idFieldShouldBeOfTypeLong() throws NoSuchFieldException {
        var idField = BaseEntity.class.getDeclaredField("id");
        assertEquals(
                Long.class,
                idField.getType(),
                "'id' field must be of type Long"
        );
    }

    // -------------------------------------------------------------------------
    // Inheritance
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("ConcreteEntity should be a subtype of BaseEntity")
    void concreteEntityShouldExtendBaseEntity() {
        var entity = new ConcreteEntity();
        assertInstanceOf(BaseEntity.class, entity);
    }

    @Test
    @DisplayName("getId() should be accessible via the BaseEntity reference")
    void getIdShouldBeAccessibleViaBaseEntityReference() throws Exception {
        BaseEntity entity = new ConcreteEntity();
        setId(entity, 99L);
        assertEquals(99L, entity.getId());
    }

    // -------------------------------------------------------------------------
    // Helper
    // -------------------------------------------------------------------------

    /**
     * Reflectively sets the private {@code id} field on a {@link BaseEntity} instance,
     * simulating what a JPA provider would do after persisting the entity.
     *
     * @param entity the target entity
     * @param value  the id value to assign
     */
    private static void setId(BaseEntity entity, Long value) throws Exception {
        Field idField = BaseEntity.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(entity, value);
    }
}
