<div class="p_nav">
	{$ClassName}.Edit:<a href="{literal}{$Request.domainName}{$Request.requestURI}?action=list{/literal}">列表</a>  &gt; 编辑
</div>

<form name="frm" method="post" action="{literal}{$Request.domainName}{$Request.requestURI}{/literal}">
{literal}{if $instance.{/literal}{$pk.propertyName}{literal} == null}{/literal}
<input type="hidden" name="action" value="create"/>
{literal}{else}{/literal}
<input type="hidden" name="action" value="update"/>
{literal}{/if}{/literal}
	<div class="input-area">
		<div class="but-row input-coner coner-gray no-b">
			<p class="i-c c-t-l"></p>
			<p class="i-c c-t-r"></p>
			<p class="box-control"></p>
			<p class="box-title">编辑</p>
			<input type="submit" class="but-new b-n-save" value="保存"/>
			<input type="button" class="but-new but-blank-s" value="取消" />
		</div>

<div class="input-row">
{foreach from=$cols item=c}
		<div class="cell size-1">
				<p class="t">{$c.comment}</p>
				<p class="v">
					<input type="text" id="{$c.propertyName}" class="text t-reduce" name="{$c.propertyName}" value="{literal}{$instance.{/literal}{$c.propertyName}{literal}|default:''}{/literal}" />
					<span class="tip-gray">0~100个字</span>
				</p>
		        <p class="c"></p>
		</div>
{/foreach}
<p class="c"></p>
</div>

	<div class="input-coner">
			<p class="i-c c-b-l"></p>
			<p class="i-c c-b-r"></p>
		</div>
	</div>
<br/>
</form>