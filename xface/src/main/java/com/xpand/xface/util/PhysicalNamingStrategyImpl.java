package com.xpand.xface.util;
import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
import java.util.Locale;
public class PhysicalNamingStrategyImpl extends PhysicalNamingStrategyStandardImpl {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Override
    public Identifier toPhysicalTableName(Identifier name, JdbcEnvironment context) {
		System.out.println("name is "+name);
        return new Identifier(name.getText(), name.isQuoted());
    }
    @Override
    public Identifier toPhysicalColumnName(Identifier name, JdbcEnvironment context) {
    	System.out.println("name is "+name);
        return new Identifier(name.getText(), name.isQuoted());
    }
//    protected static String addUnderscores(String name) {
//        final StringBuilder buf = new StringBuilder(name.replace('.', '_'));
//        for (int i = 1; i < buf.length() - 1; i++) {
//            if (Character.isLowerCase(buf.charAt(i - 1)) &&
//                Character.isUpperCase(buf.charAt(i)) &&
//                Character.isLowerCase(buf.charAt(i + 1))) {
//                buf.insert(i++, '_');
//            }
//        }
//        return buf.toString().toLowerCase(Locale.ROOT);
//    }
}