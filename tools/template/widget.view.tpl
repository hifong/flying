<div class="p_nav">
	{$ClassName}.Edit:<a href="{literal}{$Request.domainName}{$Request.requestURI}?action=list{/literal}">列表</a>  &gt; 查看
</div>
<div class="data-area">
<table class="data-grid">
{foreach from=$cols item=c}
<tr>
<td width="20%">{$c.comment}</td>
<td width="*%">{literal}{$instance.{/literal}{$c.propertyName}{literal}|default:''}{/literal}</td>
</tr>
{/foreach}
</table>
</div>
<div align="center">
	<a href="{literal}{$Request.domainName}{$Request.requestURI}?action=list{/literal}"><input type="button" value="返回" class="button" /></a> &nbsp;
</div>