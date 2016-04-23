
	<widget id="{$ClassName}.view">
		<configs>
			<config name="contentType">text/html; charset=UTF-8</config>
			<config name="encoding">UTF-8</config>
		</configs>
		<templates>
			<template name="DEFAULT">widgets/{$ClassName}.view.tpl</template>
		</templates>
	</widget>
	
	<widget id="{$ClassName}.edit">
		<configs>
			<config name="contentType">text/html; charset=UTF-8</config>
			<config name="encoding">UTF-8</config>
		</configs>
		<templates>
			<template name="DEFAULT">widgets/{$ClassName}.edit.tpl</template>
		</templates>
	</widget>
	
	<widget id="{$ClassName}.list">
		<configs>
			<config name="contentType">text/html; charset=UTF-8</config>
			<config name="encoding">UTF-8</config>
		</configs>
		<templates>
			<template name="DEFAULT">widgets/{$ClassName}.list.tpl</template>
		</templates>
	</widget>
