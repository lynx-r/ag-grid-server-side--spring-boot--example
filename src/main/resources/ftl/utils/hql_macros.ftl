<#import "filter_macros.ftl" as fm>

<#macro hqlQueryIds entityClass joinedEntities filterModel sortModel>

    select

    distinct entity.id
    <@fm.selectSort sortModel=sortModel />

    from ${entityClass} entity

    ${joinedEntities}

    <@fm.queryWhere filterModel=filterModel />
    <@fm.querySort sortModel=sortModel />

</#macro>

<#macro hqlQueryByIds whereIds orderBy entityClass joinedFetchedEntities filterModel sortModel>

    from ${entityClass} entity

    ${joinedFetchedEntities}

    ${whereIds}
    ${orderBy}

</#macro>

<#macro hqlQueryCountAll entityClass joinedEntites filterModel>

    select count(distinct entity.id) from ${entityClass} entity
    ${joinedEntites}

    <@fm.queryWhere filterModel=filterModel />

</#macro>
