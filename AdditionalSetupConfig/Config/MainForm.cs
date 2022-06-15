using System;
using System.Data;
using System.Diagnostics;
using System.IO;
using System.Windows.Forms;
using MySql.Data.MySqlClient;

namespace DatabaseConfig
{
    public partial class MainForm : Form
    {
        public MainForm()
        {
            InitializeComponent();
        }

        private void Ok_button_Click(object sender, EventArgs e)
        {

            if (!CheckConnection) return;

            // create app.config file
            try
            {
                System.IO.StreamWriter writer = new(@"..\app.config");
                writer.WriteLine("dbURL=jdbc:mysql://" + host_textbox.Text + "/goldcoast_esports?serverTimezone=UTC");
                writer.WriteLine("usrID=" + user_textbox.Text);
                writer.WriteLine("usrPWD=" + password_textbox.Text);
                writer.Close();
            }
            catch (Exception ex)
            {
                MessageBox.Show("Could not save config file" +
                    "\n" + ex.Message, "Save Config Failed!");
                return;
            }
            
            // import sql to database
            if (import_checkbox.Checked)
            {
                /* import sql file to database */
                string server = host_textbox.Text;
                string username = user_textbox.Text;
                string password = password_textbox.Text;
                string port = "3306";
                string script = File.ReadAllText(@"goldcoast_esports.sql");
                try
                {
                    using MySqlConnection conn = new($"server={server};username={username};password={password};port={port}");
                    conn.Open();
                    if (conn.State == ConnectionState.Open)
                    {
                        MySqlScript mySqlScript = new(conn, script);
                        mySqlScript.Execute();
                    }
                    if (conn.State == ConnectionState.Closed)
                    {
                        _ = MessageBox.Show("Database not connected.");
                        conn.Close();
                        return;
                    }
                    conn.Close();
                }
                catch (Exception ex)
                {
                    _ = MessageBox.Show(ex.Message + "\nPlease check details and try again", "Import Failed!");
                    return;
                }
            }

            // run java installer 
            if (java_checkbox.Checked)
            {
                try
                {
                    Process javainstall = Process.Start(new ProcessStartInfo
                    {
                        FileName = @"..\java\jdk-17_windows-x64_bin.exe",
                        Verb = "runas",
                        UseShellExecute = true
                    })!;
                    javainstall.WaitForExit();
                }
                catch (Exception ex)
                {
                    MessageBox.Show("Java Installation Failed!\n" + ex.Message);
                    return;
                }
            }
            ExitApplication();
        }

        private static void ExitApplication()
        {
            // exit the application
            if (Application.MessageLoop)
            {
                // WinForms app
                Application.Exit();
            }
            else
            {
                // Console app
                Environment.Exit(0);
            }
        }

        private void Check_button_Click(object sender, EventArgs e)
        {
            if (CheckConnection)
            {
                _ = MessageBox.Show("Connected Successfully.");
            }
        }

        private bool CheckConnection
        {
            get
            {
                /* check connection */
                string server = host_textbox.Text;
                string username = user_textbox.Text;
                string password = password_textbox.Text;
                string port = "3306";
                try
                {
                    using MySqlConnection conn = new($"server={server};username={username};password={password};port={port}");
                    conn.Open();
                    if (conn.State == ConnectionState.Open)
                    {
                        conn.Close();
                        return true;
                    }
                    if (conn.State == ConnectionState.Closed)
                    {
                        _ = MessageBox.Show("Database not connected.");
                        conn.Close();
                        return false;
                    }
                    conn.Close();
                }
                catch (Exception ex)
                {
                    _ = MessageBox.Show(ex.Message + "\nPlease check details and try again", "Connection Failed!");
                }
                return false;
            }
        }
    }
}
