package minuhy.xiaoxiang.blog.database.common;

import java.sql.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import minuhy.xiaoxiang.blog.config.DebugConfig;
/**
 * 数据库执行类
 * @author y17mm
 * 创建时间:2023-02-13 22:43 
 */
public class Executant {
	private static final Logger log = LoggerFactory.getLogger(Executant.class);
	private Connection connection;
    private PreparedStatement statement;
    private ResultSet resultSet;


    public Connection getConn() {
        try {
            if(connection == null || connection.isClosed()) {
                connection = Connector.getConnection();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            log.error("在获取数据连接时出错",e);
        }
        return connection;
    }

    /**
     * 判断数据库是否连接
     * @param connection 数据库连接
     * @return 连接/否
     */
    public static boolean isConnection(Connection connection){
        try {
            if (connection!=null && !connection.isClosed()){
                return true;
            }else {
                if(DebugConfig.isDebug){
                    log.debug("数据库没有连接 ~");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            log.error("判断数据库连接时出错",e);
        }
        return false;
    }

    public int update(String sql, String ... values) throws SQLException {
        return insert(sql,values);
    }


    public int insert(String sql, String ... values) throws SQLException {
        getConn();

        statement = connection.prepareStatement(sql);

        for(int i=0;i<values.length;i++) {
            statement.setString(i+1, values[i]);
        }

        return statement.executeUpdate();
    }

    public boolean execute(String sql) throws SQLException {
        getConn();

        statement = connection.prepareStatement(sql);
        return statement.executeUpdate() >= 0;
    }

    public ResultSet query(String sql, String ... values) throws SQLException {
        getConn();

        statement = connection.prepareStatement(sql);

        for(int i=0;i<values.length;i++) {
            statement.setString(i+1, values[i]);
        }

        resultSet = statement.executeQuery();

        return resultSet;
    }


    /**
     * 判断表是否存在
     *
     * @param conn      数据库连接
     * @param tableName 表名
     * @return true：存在。否则不存在
     */
    public static boolean isExistsTable(Connection conn, String tableName) {
        String sql = "select * from information_schema.TABLES where TABLE_NAME=?";

        if(DebugConfig.isDebug){
            log.debug("查询表是否存在：{} - {}",sql,tableName);
        }

        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            if (conn != null && !conn.isClosed()) {
                statement = conn.prepareStatement(sql);
                statement.setString(1, tableName);
                resultSet = statement.executeQuery();
                boolean res = resultSet.next();
                if(DebugConfig.isDebug){
                    log.debug("表存在查询结果：{}",res);
                }
                return res;
            } else {
                throw new NullPointerException("数据库连接为空或已关闭");
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("查询表是否存在时出错：{}", e.getMessage());
        } finally {
            release(statement, resultSet);
        }
        return false;
    }

    public void release(){
        try {
            if(this.resultSet!=null && !this.resultSet.isClosed()) {
                this.resultSet.close();
            }
            if(this.statement!=null && !this.statement.isClosed()) {
                this.statement.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            log.error("在释放数据库资源时出错",e);
        }
    }

    public void close() {
        try {
            release();
            if(this.connection!=null && !this.connection.isClosed()) {
                this.connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            log.error("在关闭数据库连接时出错",e);
        }
    }



    /**
     * 关闭数据库资源
     *
     * @param statement 句柄
     * @param resultSet 结果集
     */
    public static void release(PreparedStatement statement, ResultSet resultSet) {
        close(null, statement, resultSet);
    }

    /**
     * 关闭数据库连接
     *
     * @param conn 数据库连接
     */
    public static void close(Connection conn) {
        close(conn, null, null);
    }

    /**
     * 关闭数据库资源
     *
     * @param conn      数据库连接
     * @param statement 数据库句柄
     */
    public static void close(Connection conn, Statement statement) {
        close(conn, statement, null);
    }

    /**
     * 关闭数据库资源
     *
     * @param conn      数据库连接
     * @param statement 数据库句柄
     * @param resultSet 数据库结果集
     */
    public static void close(Connection conn, Statement statement, ResultSet resultSet) {
        try {

            if (resultSet != null && !resultSet.isClosed()) {
                resultSet.close();
            }

            if (statement != null && !statement.isClosed()) {
                statement.close();
            }

            if (conn != null && !conn.isClosed()) {
                conn.close();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
