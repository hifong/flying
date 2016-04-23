<html>
<head>
	<title>{$ClassName}</title>
	<link href="css/normal.css" rel="stylesheet" type="text/css" />
	<link href="css/theme/blue/main.css" rel="stylesheet"/>
	<link href="css/theme/blue/normal.css" rel="stylesheet"/>
	<script type="text/javascript" src="js/jquery/jquery.js"></script>
	<script type="text/javascript" src="js/framework/ListControl.js"></script>
		<script type="text/javascript">
		$(document).ready(function(){
			new ListControl();
		});
	</script>
</head>
<body>
{literal}
{foreach from=$WIDGETIDS item=wid}
{widget widgetId="$wid"}
{/foreach}
{/literal}
</body>
</html>