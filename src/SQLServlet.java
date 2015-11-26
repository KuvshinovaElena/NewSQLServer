import javax.servlet.GenericServlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

/**
 * Created by HP on 30.10.2015.
 */
public class SQLServlet extends GenericServlet {
    @Override
    public void init() {
        try{
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
            System.out.println(ex.toString());
        }
    }

    @Override
    public void service(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException {
        servletResponse.getWriter().append("<link rel=\"stylesheet\" type=\"text/css\" href=\"css/first.css\"/>");
        sendSqlForm(servletRequest, servletResponse);
    }

    private void sendSqlForm(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException{
        PrintWriter w = servletResponse.getWriter();
        w.println("<HTML>");
        w.println("<HEAD>");
        w.println("<TITLE>SQL Servlet</TITLE>");
        w.println("</HEAD>");
        w.println("<BODY>");
        w.println("<BR>Please, type your request");
        w.println("<BR> <FORM action=/sql method=service>");
        w.println("<TEXTAREA Name=sql cols=90 rows=8>");
        String sql = servletRequest.getParameter("sql");
        if(sql != null) {
            w.print(sql);
        }
        w.println("</TEXTAREA>");
        w.println("<BR>");
        w.println("<INPUT TYPE=submit value=OK>");
        w.println("</FORM>");
        w.println("<BR>");
        if(sql != null) {
            executeSql(sql.trim(), servletResponse, w);
        }
        w.println("</BODY>");
        w.println("</HTML>");
    }

    private void executeSql(String sql, ServletResponse response, PrintWriter w) throws ServletException, IOException{
        try{
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost/db_first", "root", "Passw0rd4SQL");
            Statement st = con.createStatement();
            String res = sql.substring(0, 6);
            if (res.compareToIgnoreCase("select")==0){
                ResultSet rs = null;        //Отправление запроса в базу данных и получаем данные
                try{
                   rs = st.executeQuery(sql);
                }
                catch(SQLException e){
                    response.getWriter().print("ERROR!!! Error accessing database.");
                    return;
                }
                for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                    response.getWriter().print(" - " + rs.getMetaData().getColumnName(i));
                }
                w.println("<BR>");
                response.getWriter().flush();
                while (rs.next()) {
                    for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                        response.getWriter().print(" - " + rs.getString(i));
                    }
                    w.println("<BR>");
                }
                rs.close();
            }
            else{
                if (res.compareToIgnoreCase("insert")==0){
                    try{
                        st.executeUpdate(sql);      //Отправляем запрос в базу данных
                    }
                    catch(SQLException e){
                        response.getWriter().print("ERROR!!! Error accessing database.");
                        return;
                    }
                    response.getWriter().print("The data has been successfully added to the database.");
                }
                else{
                    if (res.compareToIgnoreCase("create")==0){
                        try{
                            st.executeUpdate(sql);
                        }
                        catch(SQLException e){
                            response.getWriter().print("ERROR!!! Error accessing database.");
                            return;
                        }
                        response.getWriter().print("The new table created successfully.");
                    }
                    else
                    {
                        if (res.compareToIgnoreCase("update")==0){
                            try{
                                st.executeUpdate(sql);
                            }
                            catch(SQLException e){
                                response.getWriter().print("ERROR!!! Error accessing database.");
                                return;
                            }
                            response.getWriter().print("The data have been updated successfully.");
                        }
                        else {
                            response.getWriter().print("ERROR!!! Unknown request!");
                        }
                    }
                }
            }
            con.close();
            st.close();
        } catch (SQLException ex) {
            ex.toString();
        }
    }

}