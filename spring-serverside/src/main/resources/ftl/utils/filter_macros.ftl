<#-- @ftlvariable name="entityMapper" type="java.util.Map" -->
<#-- @ftlvariable name="filterTypeToHqlOperator" type="java.util.Map" -->
<#-- @ftlvariable name="constants" type="java.util.Map" -->

<#assign filterNameToHavingPath = entityMapper['filterNameToHavingPath']>
<#assign havingColumns = entityMapper['filterNameToHavingPath']?keys>
<#assign excludeFromWhere = entityMapper['excludeFromWhere']>
<#assign filterNameToHqlPath = entityMapper['filterNameToHqlPath']>
<#assign excludeFromWhere = entityMapper['excludeFromWhere']>
<#assign filterPrefix = entityMapper['filterPrefix']!>
<#assign filterSuffix = entityMapper['filterSuffix']!>

<#assign filterProp = "filter" >
<#assign filterDateFromProp = "dateFrom" >
<#assign filterDateToProp = "dateTo" >
<#assign filterValuesProp = "values" >

<#assign operatorTypeProp = "type" >
<#assign filterTypeProp = "filterType" >
<#assign filterOperatorProp = "operator" >

<#assign filterTypeText = "text" >
<#assign filterTypeNumber = "number" ><!-- в случае оператора equals подходит и для boolean - формат: <field> = <field_value> -->
<#assign filterTypeDate = "date" >
<#assign filterTypeSet = "set" >
<#assign filterTypeIsNotNull = "isNotNull" >
<#assign filterTypeBooleanToNullNotNull = "booleanToNullNotNull" ><!-- true=>IS NOT NULL, false=>IS NULL -->

<#function formatDate dateValue>
    <#return "'${dateValue[0..<10]}'">
</#function>

<#macro simpleFilter filterObj filterNameMapper filterName>
    <#if filterPrefix[filterName]??>
        ${filterPrefix[filterName]}
    </#if>
    <#switch filterObj[filterTypeProp]>
        <#case filterTypeNumber>
            ${filterNameMapper[filterName]!filterName}
            ${filterTypeToHqlOperator[filterObj[operatorTypeProp]]}
            ${filterObj[filterProp]}
            <#break>
        <#case filterTypeText>
            LOWER(${filterNameMapper[filterName]!filterName})
            ${filterTypeToHqlOperator[filterObj[operatorTypeProp]]}
            <#if filterTypeToHqlOperator[filterObj[operatorTypeProp]] == 'LIKE'>
                <#if filterObj[operatorTypeProp] == 'startsWith'>
                    LOWER('${filterObj[filterProp]}%')
                <#elseif filterObj[operatorTypeProp] == 'endsWith'>
                    LOWER('%${filterObj[filterProp]}')
                <#else>
                    LOWER('%${filterObj[filterProp]}%')
                </#if>
            <#else>
                LOWER('${filterObj[filterProp]}')
            </#if>
            <#break>
        <#case filterTypeIsNotNull>
            ${filterNameMapper[filterName]!filterName}
            IS NOT NULL
            <#break>
        <#case filterTypeBooleanToNullNotNull>
            ${filterNameMapper[filterName]!filterName}
            <#if filterObj[filterProp] == 'true'>
                IS NOT NULL
            <#else>
                IS NULL
            </#if>
            <#break>
        <#case filterTypeDate>
            <#if filterObj[filterDateToProp]??>
                ${filterNameMapper[filterName]!filterName}
                BETWEEN
                ${formatDate(filterObj[filterDateFromProp])}
                AND
                ${formatDate(filterObj[filterDateToProp])}
            <#else>
                TO_CHAR(${filterNameMapper[filterName]!filterName}, 'YYYY-MM-DD')
                ${filterTypeToHqlOperator[filterObj[operatorTypeProp]]}
                ${formatDate(filterObj[filterDateFromProp])}
            </#if>
            <#break>
        <#case filterTypeSet>
            <#if filterObj[filterValuesProp]?has_content>
                ${filterNameMapper[filterName]!filterName}
                IN
                (${filterObj[filterValuesProp]?map(v -> "'${v}'")?join(",")})
            <#else>
                ${filterNameMapper[filterName]!filterName}
                NOT IN
                ('')
            </#if>
            <#break>
    </#switch>
    <#if filterSuffix[filterName]??>
        ${filterSuffix[filterName]}
    </#if>
</#macro>

<#macro conditionFilter filterObj filterNameMapper filterName>
    (
    <@simpleFilter filterObj['condition1'] filterNameMapper filterName/>
    ${filterObj[filterOperatorProp]}
    <@simpleFilter filterObj['condition2'] filterNameMapper filterName/>
    )
</#macro>

<#macro queryWhere filterModel>
    <#if filterModel?has_content>
    <#-- Убираем колонки, которые нужно фильтровать через having-->
        <#if havingColumns?has_content>
            <#assign whereFilters = filterModel?keys?filter(f -> !havingColumns?seq_contains(f) && !excludeFromWhere?seq_contains(f))>
        <#else>
            <#assign whereFilters = filterModel?keys?filter(f -> !excludeFromWhere?seq_contains(f))>
        </#if>
        <#if whereFilters?has_content>
            WHERE
            <#list whereFilters as filterName>
                <#assign filterObj = filterModel[filterName]>

                <#if filterObj[filterOperatorProp]??>
                    <@conditionFilter filterObj filterNameToHqlPath filterName/>
                <#else>
                    <@simpleFilter filterObj filterNameToHqlPath filterName/>
                </#if>
                <#sep>AND
            </#list>
        </#if>
    </#if>
</#macro>

<#macro queryHaving filterModel>
    <#if filterModel?has_content>
    <#-- Получаем колонки, которые нужно фильтровать через having-->
        <#assign havingFilters = filterModel?keys?filter(fn -> havingColumns?seq_contains(fn))>
        <#if havingFilters?has_content>
            HAVING
            <#list havingFilters as filterName>
                <#assign filterObj = filterModel[filterName]>

                <#if filterObj[filterOperatorProp]??>
                    <@conditionFilter filterObj filterNameToHavingPath filterName/>
                <#else>
                    <@simpleFilter filterObj filterNameToHavingPath filterName/>
                </#if>
                <#sep>AND
            </#list>
        </#if>
    </#if>
</#macro>

<#macro queryGroupBy rowGroupCols>
    <#if rowGroupCols?has_content>
        GROUP BY
        <#list rowGroupCols as groupBy>
            ${filterNameToHqlPath[groupBy.field]!groupBy.field}
            <#sep>,
        </#list>
    </#if>
</#macro>

<#macro querySort sortModel>
    <#if sortModel?has_content>
        ORDER BY
        <#list sortModel as sortObj>
            <#if sortObj.colId?lower_case?ends_with("idasstring")>
                ${filterNameToHqlPath[sortObj.colId?remove_ending("AsString")]}
            <#else>
                ${filterNameToHqlPath[sortObj.colId]!sortObj.colId} ${sortObj.sort}
            </#if>
            <#sep>,
        </#list>
        NULLS LAST
    </#if>
</#macro>

<#macro selectSort sortModel>
    <#if sortModel?has_content>
        ,
        <#list sortModel as sortObj>
            <#if sortObj.colId?lower_case?ends_with("idasstring")>
                min(${filterNameToHqlPath[sortObj.colId?remove_ending("AsString")]})
            <#else>
                min(${filterNameToHqlPath[sortObj.colId]!sortObj.colId})
            </#if>
            <#sep>,
        </#list>
    </#if>
</#macro>

<#macro selectGroupBy rowGroupCols>
    <#if rowGroupCols?has_content>
        ,
        <#list rowGroupCols as groupBy>
            ${filterNameToHqlPath[groupBy.field]!groupBy.field}
            <#sep>,
        </#list>
    </#if>
</#macro>
