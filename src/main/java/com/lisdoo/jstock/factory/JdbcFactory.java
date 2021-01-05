package com.lisdoo.jstock.factory;

import java.sql.*;

public class JdbcFactory {

    static Connection con;
    static String sql = "select code from jstock;";
    static Statement stat;
    public static String stockList;

    public JdbcFactory() {
    }

    public static Connection get() throws ClassNotFoundException, SQLException {

        if (JdbcFactory.con == null) {
            //1、导入驱动jar包
            //2、注册驱动
            Class.forName("com.mysql.jdbc.Driver");

            //3、获取数据库的连接对象
            JdbcFactory.con = DriverManager.getConnection("jdbc:mysql://ali47:3306/jstock", "root", "lenovo.112");
            stat = con.createStatement();

            return con;
        } else {

            return JdbcFactory.con;
        }
    }

    public static void fillList() throws SQLException {

        //6、执行sql并接收返回结果
        ResultSet rs = stat.executeQuery(sql);

        StringBuffer sb = new StringBuffer();
        //7、处理结果
        while (rs.next()) {
            sb.append(
                    rs.getString("code"));
            sb.append(",");
        }
        JdbcFactory.stockList = sb.toString();
    }

    public static void release() throws SQLException {
        if (stat != null) {
            JdbcFactory.stat.close();
        }
        if (con != null) {
            JdbcFactory.con.close();
        }
        JdbcFactory.stat = null;
        JdbcFactory.con = null;
    }

    public static void main(String[] args) throws Exception {   //下面方法有不同的异常，我直接抛出一个大的异常

        //1、导入驱动jar包
        //2、注册驱动
        Class.forName("com.mysql.jdbc.Driver");

        //3、获取数据库的连接对象
        Connection con = DriverManager.getConnection("jdbc:mysql://ali47:3306/jstock", "root", "lenovo.112");

        //4、定义sql语句
        String sql = "select code from jstock;";

        //5、获取执行sql语句的对象
        Statement stat = con.createStatement();

        //6、执行sql并接收返回结果
        ResultSet rs = stat.executeQuery(sql);

        //7、处理结果
        while (rs.next()) {
            System.out.println(
                rs.getString("code"));
        }


        //8、释放资源
        stat.close();
        con.close();
    }
}
