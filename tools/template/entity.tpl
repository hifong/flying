package {$PackageName}.entity;

public class {$ClassName} implements java.io.Serializable{
	{foreach from=$cols item=col}
	/**
	 * {$col.comment}
	 */
	private {$col.propertyType} {$col.propertyName};
	{/foreach}

	{foreach from=$cols item=col}
	public {$col.propertyType} {$col.getterName}() {
		return this.{$col.propertyName};
	}

	public void {$col.setterName}({$col.propertyType} val) {
		this.{$col.propertyName} = val;
	}
	{/foreach}
}