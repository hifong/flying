<script>
                  function redirect(page){
                 var url= "{literal}{$Request.domainName}{$Request.requestURI}?action={$action | default : ""}&id={$id | default:""}&name={$c.title  | default:""}{/literal}"+page;
                 window.location=url;
                 }
</script>
{literal}{if $list eq "[]"}{/literal}
	<p align="center">暂时没有数据</p>
{literal}{else}{/literal}
		<div class="page-info-bg">
			<div class="inner">
{literal}{if $page eq "1"}{/literal}
	<a class="text">首页</a>
	<a class="text">上一页</a>
{literal}{else}{/literal}
	<a class="text" onclick="{literal}javascript:redirect('&page=1');{/literal}">首页</a>
	<a class="text" onclick="{literal}javascript:redirect('&page={$page-1}');{/literal}">上一页</a>
{literal}{/if}{/literal}
{literal}{foreach from=$plist item=p}{/literal}
	{literal}{if $p.index eq $page}{/literal}
		<a href="javascript:none();" class="high">{literal}{$p.name}{/literal}</a>
	{literal}{else}{/literal}
	    <a href="{literal}javascript:redirect('&page={$p.index}');{/literal}">{literal}{$p.name}{/literal}</a>
{literal}{/if}{/literal}
{literal}{/foreach}{/literal}
{literal}{if $page eq $countPage} {/literal}
	<a class="text">下一页</a>
	<a class="text">末页</a>
{literal}{else}{/literal}
	<a class="text" onclick="{literal}javascript:redirect('&page={$page+1}');{/literal}">下一页</a>
	<a class="text" onclick="{literal}javascript:redirect('&page={$countPage}');{/literal}">末页</a>
{literal}{/if}{/literal}
<span>总共{literal}{$countPage}{/literal}页</span>
</div>
		</div>

{literal}{/if}{/literal}