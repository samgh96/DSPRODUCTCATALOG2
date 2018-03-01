package org.tmf.dsmapi.catalog.service;

import javax.ejb.EJB;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.ws.rs.Path;
import javax.ws.rs.core.UriInfo;
import org.tmf.dsmapi.catalog.resource.AbstractEntity;
import org.tmf.dsmapi.catalog.resource.LifecycleStatus;
import org.tmf.dsmapi.catalog.exception.IllegalLifecycleStatusException;
import org.tmf.dsmapi.commons.FieldSelector;
import org.tmf.dsmapi.commons.ParsedVersion;
import org.tmf.dsmapi.commons.PropertiesSingleton;
import org.tmf.dsmapi.commons.QueryParameterParser;
import org.tmf.dsmapi.commons.ReferencedEntityGetter;

/**
 *
 * @author bahman.barzideh
 *
 */
public abstract class AbstractFacadeREST<T extends AbstractEntity> {
    private final FieldSelector fieldSelector;
    private final ReferencedEntityGetter<T> referencedEntityGetter;

    @EJB
    private PropertiesSingleton properties;

    /*
     *
     */
    protected AbstractFacadeREST(Class theClass) throws IllegalArgumentException {
        fieldSelector = new FieldSelector(theClass);
        referencedEntityGetter = new ReferencedEntityGetter<T>(theClass);
    }

    /*
     *
     */
    public abstract Logger getLogger();

    /*
     *
     */
    public String getRelativeEntityContext() {
        Path path = getClass().getAnnotation(Path.class);
        String value = (path != null) ? path.value() :  null;
        if (value == null) {
            return null;
        }

        int index = value.lastIndexOf("/");
        return (index >= 0) ? value.substring(index + 1) : value;
    }

    /*
     *
     */
    protected void validateLifecycleStatus(AbstractEntity newEntity, AbstractEntity existingEntity) throws IllegalLifecycleStatusException {
        if (newEntity == null) {
            throw new IllegalArgumentException ("newEntity is required");
        }

        if (existingEntity == null) {
            throw new IllegalArgumentException ("existingEntity is required");
        }

        if (newEntity.canLifecycleTransitionFrom (existingEntity.getLifecycleStatus()) == true) {
            return;
        }

        getLogger().log(Level.FINE, "invalid lifecycleStatus transition: {0} => {1}", new Object[]{existingEntity.getLifecycleStatus(), newEntity.getLifecycleStatus()});
        Set<LifecycleStatus> statusList = LifecycleStatus.transitionableStatues(existingEntity.getLifecycleStatus());
        if (statusList == null) {
            throw new IllegalLifecycleStatusException(existingEntity.getLifecycleStatus());
        }

        throw new IllegalLifecycleStatusException(existingEntity.getLifecycleStatus(), statusList);
    }

    /*
     *
     */
    public String buildHref(UriInfo uriInfo, String id, ParsedVersion parsedVersion) {
        // Get default server param
        String baseUrl = properties.getServer();

        if (baseUrl == null) {
            // If the URL is not configured in the properties use UriInfo object
            URI uri = (uriInfo != null) ? uriInfo.getBaseUri() : null;
            baseUrl = (uri != null) ? uri.toString() : null;
        }

        if (baseUrl == null) {
            return null;
        }

        if (!baseUrl.endsWith("/")) {
            baseUrl += "/";
        }

        baseUrl += getRelativeEntityContext() + "/";
        if (id == null || id.length() <= 0) {
            return (baseUrl);
        }

        baseUrl += id;
        String version = (parsedVersion != null) ? parsedVersion.getExternalView() : null;
        if (version == null || version.length() <= 0) {
            return baseUrl;
        }

        return baseUrl + ":(" + version + ")";
    }

    /*
     *
     */
    public Set<String> getFieldSet(QueryParameterParser queryParameterParser) {
        Set<String> fieldSet = new HashSet<>();
        if (queryParameterParser == null) {
            return fieldSet;
        }

        List<String> queryParameterField = queryParameterParser.removeTagWithValues(ServiceConstants.QUERY_KEY_FIELD_ESCAPE + ServiceConstants.QUERY_KEY_FIELD);
        if (queryParameterField == null) {
            queryParameterField = queryParameterParser.removeTagWithValues(ServiceConstants.QUERY_KEY_FIELD);
        }

        if (queryParameterField != null && !queryParameterField.isEmpty()) {
            String queryParameterValue = queryParameterField.get(0);
            fieldSet.addAll(breakFieldList_(queryParameterValue));
        }

        return fieldSet;
    }

    /*
     *
     */
    protected void getReferencedEntities(T entity, int depth) {
        referencedEntityGetter.getReferencedEntities(entity, depth);
    }

    /*
     *
     */
    protected void getReferencedEntities(Set<T> entities, int depth) {
        referencedEntityGetter.getReferencedEntities(entities, depth);
    }

    /*
     *
     */
    protected Object selectFields(T inputEntity, Set<String> outputFields) {
        outputFields.add(ServiceConstants.ID_FIELD);

        return fieldSelector.selectFields(inputEntity, outputFields);
    }

    protected ArrayList<Object> selectFields(Set<T> inputEntities, Set<String> outputFields) {
       outputFields.add(ServiceConstants.ID_FIELD);

       ArrayList<Object> outputEntities = new ArrayList<>();
       inputEntities.stream().forEach((entity) -> {
           outputEntities.add(fieldSelector.selectFields(entity, outputFields));
        });

       return outputEntities;
    }

    protected ArrayList<Object> selectFields(List<T> inputEntities, Set<String> outputFields) {
       outputFields.add(ServiceConstants.ID_FIELD);

       ArrayList<Object> outputEntities = new ArrayList<>();
       inputEntities.stream().forEach((entity) -> {
           outputEntities.add(fieldSelector.selectFields(entity, outputFields));
        });

       return outputEntities;
    }

    protected List<T> sortByProvidedIds (List<String> ids, List<T> entities) {
        return entities.stream()
                .sorted((e1, e2) ->
                        Integer.compare(ids.indexOf(e1.getId()), ids.indexOf(e2.getId())))
                .collect(Collectors.toList());
    }

    private List<String> breakFieldList_(String input) {
        if (input == null) {
            return new ArrayList<>();
        }

        String tokenArray [] = input.split(",");
        return Arrays.asList(tokenArray);
    }
}