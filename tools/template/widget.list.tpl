	<script type="text/javascript" src="js/util.js"></script>
{if $message != null }<div style="text-indent:.6em;"><font class="red">{$message}</font></div>{/if}
<div class="input-area">
<form name="frm" action="{literal}{$Request.domainName}{$Request.requestURI}{/literal}" method="post">
   <input type="hidden" name="action" value="search" />
		<div class="but-row input-coner coner-gray no-b">
			<p class="i-c c-t-l"></p>
			<p class="i-c c-t-r"></p>
			<p class="box-control"></p>
			<p class="box-title">搜索条件</p>
			<input type="submit" class="but-new b-n-search" value="搜索"/>
		</div>
		
		<div class="input-row">
		    {foreach from=$cols item=c}
			<div class="cell size-4">
				<p class="t">{$c.propertyName}：</p>
				<p class="v"><input type="text" value="{literal}{$instance.{/literal}{$c.propertyName}{literal}|default:""}{/literal}" name="{$c.propertyName}" class="text" /></p>
				<p class="c"></p>
			</div>
			<p class="c"></p>
			{/foreach}
		</div>

		<div class="input-coner">
			<p class="i-c c-b-l"></p>
			<p class="i-c c-b-r"></p>
		</div>
		</form>
	</div>
<div class="data-area">
<form name="frm" action="{literal}{$Request.domainName}{$Request.requestURI}{/literal}" method="post">
<input type="hidden" name="action" value="delete" />
	<div class="but-row input-coner coner-gray data-but">
			<p class="i-c c-t-l"></p>
			<p class="i-c c-t-r"></p>
			<input type="button" class="but-new b-n-new" value="新建" onclick="javascript:window.location='{literal}{$Request.domainName}{$Request.requestURI}?action=edit{/literal}'" />
			<input type="button" onclick="dele()" class="but-new b-n-del" value="删除"/>
	</div>
		<div class="data-con">
		<table class="data-grid">
		<thead>
		<tr>
		<td><a href="javascript:ck1()">全/反选</a></td>
		{foreach from=$cols item=c}
			<td>{$c.comment}</td>
		{/foreach}
		<td>操作</td>
		</tr>
		</thead>
			{literal}{foreach from=$instances item=r}{/literal}
			<tr>
			<td align="center">
			<input type="checkbox" name="check1"  value="{literal}{$r.{/literal}{$pk.propertyName}{literal}|default:""}{/literal}" /> </td>
			{foreach from=$cols item=c}
			<td align="center">{literal}{$r.{/literal}{$c.propertyName}{literal}|default:""}{/literal}</td>
		{/foreach}
		<td><a href="{literal}{$Request.domainName}{$Request.requestURI}?action=edit&{/literal}{$pk.propertyName}={literal}{$r.{/literal}{$pk.propertyName}{literal}|default:"&nbsp;"}{/literal}">Edit</a></td>
		</tr>
		{literal}{/foreach}{/literal}
		</table>
</div>
	<div class="but-row input-coner coner-gray data-but">
			<p class="i-c c-b-l"></p>
			<p class="i-c c-b-r"></p>
			{literal}{include file="../include/include.tpl"}{/literal}
		</div>
</div>
</div>
</form>