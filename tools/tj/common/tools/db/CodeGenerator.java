package tj.common.tools.db;

import java.io.File;
import java.io.FileOutputStream;

import org.apache.commons.lang.StringUtils;
import org.lilystudio.smarty4j.Context;
import org.lilystudio.smarty4j.Engine;
import org.lilystudio.smarty4j.Template;

import tj.common.config.ConfigUtils;

public class CodeGenerator {

    public void export(String templateDir, String targetDir, String packageName, String tableName) throws Exception {
        Engine engine = new Engine();
        Context context = new Context();
        UserTab tab = new UserTab(tableName);
        context.set("PackageName", packageName);
        context.set("ClassName", tab.getClassName());
        context.set("TableName", tab.getTableName());
        context.set("cols", tab.getCols());
        context.set("pk", tab.getIdProperty());
        context.set("last", tab.getLastProperty());

        engine.setTemplatePath(templateDir);

        this.validPath(targetDir);

        this.export(engine, context, "entity.tpl", targetDir, packageName, new String[] { "entity",
                tab.getClassName() + ".java" });
        this.export(engine, context, "dao.tpl", targetDir, packageName, new String[] { "dao",
                tab.getClassName() + "DAO.java" });
        this.export(engine, context, "service.tpl", targetDir, packageName,
                new String[] { "service", tab.getClassName() + "Service.java" });
        this.export(engine, context, "service.impl.tpl", targetDir, packageName,
                new String[] { "service", "impl", tab.getClassName() + "ServiceImpl.java" });
        this.export(engine, context, "action.tpl", targetDir, packageName, new String[] { "action",
                tab.getClassName() + "Action.java" });

        this.export(engine, context, "ibatis.tpl", targetDir, packageName, new String[] { "config",tab.getClassName()+".ibatis.xml" });
        this.export(engine, context, "spring.tpl", targetDir, packageName, new String[] { "config","spring.xml" },true);
       // this.export(engine, context, "model.tpl", targetDir, packageName, new String[] { "config","model.js" });
        this.export(engine, context, "action.config.tpl", targetDir, packageName, new String[] { "config", "action.xml" },true);
        this.export(engine, context, "page.config.tpl", targetDir, packageName, new String[] { "config", "page.xml" },true);
        this.export(engine, context, "widget.config.tpl", targetDir, packageName, new String[] { "config", "widget.xml" },true);

        this.export(engine, context, "page.tpl", targetDir, packageName,
                new String[] { "template", "page", tab.getClassName() + ".tpl" });
        this.export(engine, context, "widget.list.tpl", targetDir, packageName, new String[] { "template", "widgets",
                tab.getClassName() + ".list.tpl" });
        this.export(engine, context, "widget.edit.tpl", targetDir, packageName, new String[] { "template", "widgets",
                tab.getClassName() + ".edit.tpl" });
        this.export(engine, context, "widget.view.tpl", targetDir, packageName, new String[] { "template", "widgets",
                tab.getClassName() + ".view.tpl" });
        this.export(engine, context, "include.tpl", targetDir, packageName, new String[] { "template", "include",
        		"include.tpl"});

    }
    
    public void export(Engine engine, Context context, String templateFile, String targetDir, String packageName,
            String[] paths) throws Exception {
    	export(engine,context,templateFile,targetDir,packageName,paths,false);
    }

    public void export(Engine engine, Context context, String templateFile, String targetDir, String packageName,
            String[] paths,boolean append) throws Exception {
        try {
            Template template = engine.getTemplate(templateFile);

            String dir = targetDir;

            String[] tmps = StringUtils.split(packageName, ".");
            for (String tmp : tmps) {
                dir += ConfigUtils.fileSeparator + tmp;
            }
            for (int i = 0; i < paths.length - 1; i++) {
                dir += ConfigUtils.fileSeparator + paths[i];
            }
            this.validPath(dir);
            //

            File file = new File(dir + "/" + paths[paths.length - 1]);
            FileOutputStream out = new FileOutputStream(file, append);
            template.merge(context, out);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void validPath(String dir) {
        File f = new File(dir);
        if (!f.exists()) {
            f.mkdirs();
        }
    }

    public static void main(String[] args) throws Exception {
        CodeGenerator cc = new CodeGenerator();
        final String[] tableNames = { 
//        		"AD_AP_CHARGING_BUSINESS", 		"AD_ADVERTIZER", 	"AD_AGENT", 				"AD_CONTEXT", 
//        		"AD_RECHARGE", 		"AD_PAYMENT"
"AD_AP_CHARGING_BUSINESS"
        		};
        for (String tableName : tableNames) {
            cc.export("D:/eclipse/mm/osp/frameworksrc/cfg/template/", "D:/eclipse/mm/osp/frameworksrc/export",
                    "com.aspire.bss", tableName.toUpperCase());
        }
        System.out.println("Finished!");
    }
}
