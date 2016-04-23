package com.flying.des;

import com.flying.common.des.DESPlus;

import sun.misc.BASE64Decoder;

public class TestDESPlus {
	public static void main(String[] args) throws Exception {
		BASE64Decoder decoder = new BASE64Decoder();
		
		DESPlus d3 = new DESPlus("Ue2g_jTv");
		System.out.println(d3.decrypt("111fa8d50b27d4b6169d538ce9ecd8ba"));
		System.out.println(d3.decrypt("64d6cf92a4b1b70c26eae0e71ff7e521"));
		//
		DESPlus d2 = new DESPlus("HelloWorld");
		String s = "561fb5d0083394ec87e825ab802b37d4843b8b34dac7e14472a2141a9f14248c36a8664b1baaffd33a3df34d59244b2" +
				"aba57b7ecfaea85223a874a582443c8e9c29547669423d7005896464bd988e557566b53bb1b5c6d89decf79775bb6461264dd791c455007ed52673234cc5cdcd8c4a"+
				"8cc858d983a358727556ad1559ac0bb3197cb84b811ce3cd60dd49620420f2debd44aea283c6ffeb22e6b20b7e129d1156527990c8092bfa566cdd5d701b885fd193"+
				"70bf0d87d169ed229f0ccf75f28d063a8951d9ac7dc4682114a60e1f070d718a429e8b082ea89484debd1fa786064a9db46989b227ef4a14487b8452d62c460ff09a"+
				"6589fbeaf6e9412f8e4ed83f21b73801acc8c7257b84030c4b09a8b898b21e444972be17e10e621509f1f4cc6081723f296b562ee2454c42618ae";
		System.out.println(d2.decrypt(s));
		s = "jdbc:oracle:thin:@(DESCRIPTION =  (ENABLE=BROKEN) (ADDRESS_LIST =  (LOAD_BALANCE = ON)  (FAILOVER = ON)(ADDRESS = (PROTOCOL = TCP)(HOST = 10.101.10.15)(PORT = 1521))(ADDRESS = (PROTOCOL = TCP)(HOST = 10.101.10.16)(PORT = 1521))  ) (CONNECT_DATA =(SERVICE_NAME = mmportal)  (FAILOVER_MODE =  (TYPE=SELECT)  (METHOD=BASIC)  (retries=5)(delay=5)  ) ))";
		System.out.println(d2.encrypt(s));
		
		DESPlus d = new DESPlus("eripsaye", "DES/ECB/NoPadding");
		System.out.println(new String(d.decrypt(decoder.decodeBuffer("SpEFRoSE4Qo="))));
		System.out.println(new String(d.decrypt(decoder.decodeBuffer("pgc5Sp06Uz0PTu9QyuPi4A=="))));
		
		try {
			String[] ts = {"312356", "hw12345", "key", "13622223333", "15914250657"};
			DESPlus des = new DESPlus("HelloWorld");
			System.out.println(des.decrypt("1a0bd5cf355e3fd1"));
			System.out.println(des.decrypt("2207dbfdecda7726787abf744c531497"));
			System.out.println(des.decrypt("561fb5d0083394ec87e825ab802b37d4843b8b34dac7e14472a2141a9f14248c36a8664b1baaffd33a3df34d59244b2aba57b7ecfaea85223a874a582443c8e9c29547669423d7005896464bd988e557566b53bb1b5c6d89decf79775bb6461264dd791c455007ed52673234cc5cdcd8c4a8cc858d983a358727556ad1559ac05b46fc77defcbf8a3cd60dd49620420f2debd44aea283c6ffeb22e6b20b7e129d1156527990c8092bfa566cdd5d701b885fd19370bf0d87d6f23898d7bea407c28d063a8951d9ac7dc4682114a60e1f070d718a429e8b082ea89484debd1fa78edb5c59390c641b34a3eb48149bea4387292a27c1305958064439dbbd2d1d8c2c148de34b1c91dbeba47f2e0899e0d6c16440b81dfe0fab51ba91c939ecbce1423ed6e5d3b7fb5fcb8ef1638fedb3e7d"));
			for(String t: ts) {
				String es = des.encrypt(t);
				String ds = des.decrypt(es);
				System.out.println(t + "\t = " + es + "\t = " + ds);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}