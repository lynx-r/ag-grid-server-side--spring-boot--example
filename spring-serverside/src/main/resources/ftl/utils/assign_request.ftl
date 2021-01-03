<#-- @ftlvariable name="request" type="com.example.aggridserversidespringbootexample.domain.request.TableRequest" -->
<#-- @ftlvariable name="whereIds" type="java.lang.String" -->
<#-- @ftlvariable name="orderBy" type="java.lang.String" -->
<#-- @ftlvariable name="entityClass" type="java.lang.String" -->

<#assign filterModel = request.getFilterModel()>
<#assign sortModel = request['sortModel']![]>
<#assign rowGroupCols = request['rowGroupCols']![]>
<#assign entityClass = entityClass>
<#assign whereIds = whereIds!""/>
<#assign orderBy = orderBy!""/>

<#import "constants.ftl" as const/>
