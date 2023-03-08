package minuhy.xiaoxiang.blog.database.common;

import java.sql.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import minuhy.xiaoxiang.blog.config.DebugConfig;
/**
 * ���ݿ�ִ����
 * @author y17mm
 * ����ʱ��:2023-02-13 22:43 
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
            log.error("�ڻ�ȡ��������ʱ����",e);
        }
        return connection;
    }

    /**
     * �ж����ݿ��Ƿ�����
     * @param connection ���ݿ�����
     * @return ����/��
     */
    public static boolean isConnection(Connection connection){
        try {
            if (connection!=null && !connection.isClosed()){
                return true;
            }else {
                if(DebugConfig.isDebug){
                    log.debug("���ݿ�û������ ~");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            log.error("�ж����ݿ�����ʱ����",e);
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
     * �жϱ��Ƿ����
     *
     * @param conn      ���ݿ�����
     * @param tableName ����
     * @return true�����ڡ����򲻴���
     */
    public static boolean isExistsTable(Connection conn, String tableName) {
        String sql = "select * from information_schema.TABLES where TABLE_NAME=?";

        if(DebugConfig.isDebug){
            log.debug("��ѯ���Ƿ���ڣ�{} - {}",sql,tableName);
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
                    log.debug("����ڲ�ѯ�����{}",res);
                }
                return res;
            } else {
                throw new NullPointerException("���ݿ�����Ϊ�ջ��ѹر�");
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("��ѯ���Ƿ����ʱ����{}", e.getMessage());
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
            log.error("���ͷ����ݿ���Դʱ����",e);
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
            log.error("�ڹر����ݿ�����ʱ����",e);
        }
    }



    /**
     * �ر����ݿ���Դ
     *
     * @param statement ���
     * @param resultSet �����
     */
    public static void release(PreparedStatement statement, ResultSet resultSet) {
        close(null, statement, resultSet);
    }

    /**
     * �ر����ݿ�����
     *
     * @param conn ���ݿ�����
     */
    public static void close(Connection conn) {
        close(conn, null, null);
    }

    /**
     * �ر����ݿ���Դ
     *
     * @param conn      ���ݿ�����
     * @param statement ���ݿ���
     */
    public static void close(Connection conn, Statement statement) {
        close(conn, statement, null);
    }

    /**
     * �ر����ݿ���Դ
     *
     * @param conn      ���ݿ�����
     * @param statement ���ݿ���
     * @param resultSet ���ݿ�����
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
