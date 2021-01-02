<#import "../utils/hql_macros.ftl" as hql>

<#include "../utils/assign_request.ftl">

<@hql.hqlQueryByIds
whereIds=whereIds
orderBy=orderBy
sortModel=sortModel
filterModel=filterModel
entityClass=entityClass
joinedFetchedEntities=const[entityClass?lower_case + "JoinedFetchedEntities"]!""
/>
