package tj.common.tools.db;
import tj.common.des.DESPlus;


/**
 * @author liuyuan 
 * @date   2012-12-6 下午2:53:37
 *
 */
public class Database {
	
	

	public static void main(String[] args) throws Exception {
		System.setProperty("APP_CONFIG_HOME", "D:/eclipse/mm/osp/configs/");
		
		String username = "mm_capmgr";
		String password = "Cz_eA7B2tq";
		String url = "jdbc:oracle:thin:@(DESCRIPTION=(ENABLE=BROKEN)(ADDRESS_LIST=(LOAD_BALANCE=ON)(FAILOVER=ON)(ADDRESS=(PROTOCOL=TCP)(HOST=10.101.10.13)(PORT=1521))(ADDRESS=(PROTOCOL=TCP)(HOST=10.101.10.14)(PORT=1521)))(CONNECT_DATA=(SERVICE_NAME=mmportal)(FAILOVER_MODE=(TYPE=SELECT)(METHOD=BASIC)(retries=5)(delay=5))))";

		
		DESPlus des = new DESPlus("HelloWorld");
		System.out.println(des.encrypt(username));
		System.out.println(des.encrypt(password));
		System.out.println(des.encrypt(url));
	}
}
