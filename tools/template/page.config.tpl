	<page id="{$ClassName}">
		<title>{$ClassName}</title>
		<serviceId>{$ClassName}Action</serviceId>
		<widgetIds>{$ClassName}.list</widgetIds>
		<contentType>text/html; charset=UTF-8</contentType>
		<encoding>UTF-8</encoding>
		<interceptors></interceptors>
		<templates>
			<template name="1">page/{$ClassName}.tpl</template>
		</templates>
	</page>
