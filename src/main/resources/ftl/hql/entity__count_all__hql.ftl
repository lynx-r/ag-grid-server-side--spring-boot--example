<#import "../utils/hql_macros.ftl" as hql>

<#include "../utils/assign_request.ftl">

<@hql.hqlQueryCountAll
filterModel=filterModel
entityClass=entityClass
joinedEntites=const[entityClass?lower_case + "JoinedEntities"]!""
/>
