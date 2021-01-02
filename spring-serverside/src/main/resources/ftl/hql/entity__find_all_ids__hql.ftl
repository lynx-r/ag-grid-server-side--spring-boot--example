<#import "../utils/hql_macros.ftl" as hql>

<#include "../utils/assign_request.ftl">

<@hql.hqlQueryIds
sortModel=sortModel
filterModel=filterModel
entityClass=entityClass
joinedEntities=const[entityClass?lower_case + "JoinedEntities"]!""
/>
