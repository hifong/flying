<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE sqlMap PUBLIC "-//iBATIS.com//DTD SQL Map 2.0//EN" 
"http://ibatis.apache.org/dtd/sql-map-2.dtd">
<sqlMap namespace="{$ClassName}">
	<typeAlias alias="{$ClassName}" type="{$PackageName}.entity.{$ClassName}" />

	<resultMap id="{$ClassName}Map" class="{$ClassName}">
	
		{foreach from=$cols item=col}<result property="{$col.propertyName}" column="{$col.columnName}" />
		{/foreach}
	</resultMap>

	<select id="find{$ClassName}All" resultMap="{$ClassName}Map">
		SELECT c.* FROM {$TableName} c
    </select>
	<select id="find{$ClassName}ByPrimaryKey" resultMap="{$ClassName}Map">
		SELECT c.* FROM {$TableName} c WHERE {$pk.columnName}=#{$pk.propertyName}#
    </select>
	<select id="find{$ClassName}ByProperty" resultMap="{$ClassName}Map" parameterClass="{$ClassName}">
		SELECT c.* FROM {$TableName} c
		<dynamic prepend="WHERE">
			{foreach from=$cols item=col}
            <isNotEmpty prepend=" AND " property="{$col.propertyName}">
            	c.{$col.columnName}=#{$col.propertyName}#
            </isNotEmpty>
			{/foreach}
        </dynamic>
    </select>
	<select id="find{$ClassName}ByMap" resultMap="{$ClassName}Map" parameterClass="java.util.Map">
		SELECT c.* FROM {$TableName} c
		<dynamic prepend="WHERE">
			{foreach from=$cols item=col}
            <isNotEmpty prepend=" AND " property="{$col.propertyName}">
            	c.{$col.columnName}=#{$col.propertyName}#
            </isNotEmpty>
			{/foreach}
        </dynamic>
    </select>
	<select id="findRowCount{$ClassName}ByProperty" resultClass="java.lang.Integer" parameterClass="{$ClassName}">
		SELECT count(1) FROM {$TableName} c
		<dynamic prepend="WHERE">
			{foreach from=$cols item=col}
            <isNotEmpty prepend=" AND " property="{$col.propertyName}">
            	c.{$col.columnName}=#{$col.propertyName}#
            </isNotEmpty>
			{/foreach}
        </dynamic>
    </select>
	<select id="findRowCount{$ClassName}ByMap" resultClass="java.lang.Integer" parameterClass="java.util.Map">
		SELECT count(1) FROM {$TableName} c
		<dynamic prepend="WHERE">
			{foreach from=$cols item=col}
            <isNotEmpty prepend=" AND " property="{$col.propertyName}">
            	c.{$col.columnName}=#{$col.propertyName}#
            </isNotEmpty>
			{/foreach}
        </dynamic>
    </select>
    
	<insert id="insert{$ClassName}" parameterClass="{$ClassName}">
		INSERT INTO {$TableName}
		( {foreach from=$cols item=col}{$col.columnName} {if $col.columnName neq $last.columnName},{/if}  {/foreach} )
		VALUES 
		( {foreach from=$cols item=col}#{$col.propertyName}#{if $col.columnName neq $last.columnName},{/if}  {/foreach} )
	 </insert>

	<update id="update{$ClassName}" parameterClass="{$ClassName}">
		UPDATE {$TableName}
		SET
		<dynamic prepend=" ">
			{foreach from=$cols item=c}{if $c.propertyName neq $pk.propertyName}
			<isNotEmpty prepend=" , " property="{$c.propertyName}">
				{$c.columnName}=#{$c.propertyName}#
			</isNotEmpty>{/if}{/foreach}
		</dynamic>
		WHERE {$pk.columnName} = #{$pk.propertyName}#
	</update>
	
	<delete id="delete{$ClassName}" parameterClass="{$pk.propertyType}">
		DELETE FROM {$TableName}
		WHERE {$pk.columnName} = #value#
	</delete>
</sqlMap>
