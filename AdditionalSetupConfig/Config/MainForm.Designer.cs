namespace DatabaseConfig
{
    partial class MainForm
    {
        /// <summary>
        ///  Required designer variable.
        /// </summary>
        private System.ComponentModel.IContainer components = null;

        /// <summary>
        ///  Clean up any resources being used.
        /// </summary>
        /// <param name="disposing">true if managed resources should be disposed; otherwise, false.</param>
        protected override void Dispose(bool disposing)
        {
            if (disposing && (components != null))
            {
                components.Dispose();
            }
            base.Dispose(disposing);
        }

        #region Windows Form Designer generated code

        /// <summary>
        ///  Required method for Designer support - do not modify
        ///  the contents of this method with the code editor.
        /// </summary>
        private void InitializeComponent()
        {
            this.main_panel = new System.Windows.Forms.Panel();
            this.java_checkbox = new System.Windows.Forms.CheckBox();
            this.info_label = new System.Windows.Forms.Label();
            this.check_button = new System.Windows.Forms.Button();
            this.ok_button = new System.Windows.Forms.Button();
            this.import_checkbox = new System.Windows.Forms.CheckBox();
            this.password_textbox = new System.Windows.Forms.TextBox();
            this.user_textbox = new System.Windows.Forms.TextBox();
            this.host_textbox = new System.Windows.Forms.TextBox();
            this.password_label = new System.Windows.Forms.Label();
            this.user_label = new System.Windows.Forms.Label();
            this.host_label = new System.Windows.Forms.Label();
            this.dbconn_label = new System.Windows.Forms.Label();
            this.main_panel.SuspendLayout();
            this.SuspendLayout();
            // 
            // main_panel
            // 
            this.main_panel.Controls.Add(this.java_checkbox);
            this.main_panel.Controls.Add(this.info_label);
            this.main_panel.Controls.Add(this.check_button);
            this.main_panel.Controls.Add(this.ok_button);
            this.main_panel.Controls.Add(this.import_checkbox);
            this.main_panel.Controls.Add(this.password_textbox);
            this.main_panel.Controls.Add(this.user_textbox);
            this.main_panel.Controls.Add(this.host_textbox);
            this.main_panel.Controls.Add(this.password_label);
            this.main_panel.Controls.Add(this.user_label);
            this.main_panel.Controls.Add(this.host_label);
            this.main_panel.Controls.Add(this.dbconn_label);
            this.main_panel.Location = new System.Drawing.Point(6, 6);
            this.main_panel.Margin = new System.Windows.Forms.Padding(2, 1, 2, 1);
            this.main_panel.Name = "main_panel";
            this.main_panel.Size = new System.Drawing.Size(418, 315);
            this.main_panel.TabIndex = 0;
            // 
            // java_checkbox
            // 
            this.java_checkbox.AutoSize = true;
            this.java_checkbox.Location = new System.Drawing.Point(141, 92);
            this.java_checkbox.Name = "java_checkbox";
            this.java_checkbox.Size = new System.Drawing.Size(97, 19);
            this.java_checkbox.TabIndex = 11;
            this.java_checkbox.Text = "Install Java 17";
            this.java_checkbox.UseVisualStyleBackColor = true;
            // 
            // info_label
            // 
            this.info_label.AutoSize = true;
            this.info_label.Location = new System.Drawing.Point(65, 21);
            this.info_label.Margin = new System.Windows.Forms.Padding(2, 0, 2, 0);
            this.info_label.Name = "info_label";
            this.info_label.Size = new System.Drawing.Size(288, 60);
            this.info_label.TabIndex = 10;
            this.info_label.Text = "This application requires a Java Runtime Environment\r\nand connection to a MySQL d" +
    "atabase.\r\n\r\nPlease choose options below:";
            // 
            // check_button
            // 
            this.check_button.Location = new System.Drawing.Point(141, 276);
            this.check_button.Margin = new System.Windows.Forms.Padding(2, 1, 2, 1);
            this.check_button.Name = "check_button";
            this.check_button.Size = new System.Drawing.Size(127, 22);
            this.check_button.TabIndex = 9;
            this.check_button.Text = "Test Connection";
            this.check_button.UseVisualStyleBackColor = true;
            this.check_button.Click += new System.EventHandler(this.Check_button_Click);
            // 
            // ok_button
            // 
            this.ok_button.Location = new System.Drawing.Point(283, 276);
            this.ok_button.Margin = new System.Windows.Forms.Padding(2, 1, 2, 1);
            this.ok_button.Name = "ok_button";
            this.ok_button.Size = new System.Drawing.Size(66, 22);
            this.ok_button.TabIndex = 8;
            this.ok_button.Text = "Ok";
            this.ok_button.UseVisualStyleBackColor = true;
            this.ok_button.Click += new System.EventHandler(this.Ok_button_Click);
            // 
            // import_checkbox
            // 
            this.import_checkbox.AutoSize = true;
            this.import_checkbox.Location = new System.Drawing.Point(141, 115);
            this.import_checkbox.Margin = new System.Windows.Forms.Padding(2, 1, 2, 1);
            this.import_checkbox.Name = "import_checkbox";
            this.import_checkbox.Size = new System.Drawing.Size(113, 19);
            this.import_checkbox.TabIndex = 7;
            this.import_checkbox.Text = "Import Database";
            this.import_checkbox.UseVisualStyleBackColor = true;
            // 
            // password_textbox
            // 
            this.password_textbox.Location = new System.Drawing.Point(141, 239);
            this.password_textbox.Margin = new System.Windows.Forms.Padding(2, 1, 2, 1);
            this.password_textbox.Name = "password_textbox";
            this.password_textbox.Size = new System.Drawing.Size(208, 23);
            this.password_textbox.TabIndex = 6;
            this.password_textbox.Text = "Password";
            // 
            // user_textbox
            // 
            this.user_textbox.Location = new System.Drawing.Point(141, 206);
            this.user_textbox.Margin = new System.Windows.Forms.Padding(2, 1, 2, 1);
            this.user_textbox.Name = "user_textbox";
            this.user_textbox.Size = new System.Drawing.Size(208, 23);
            this.user_textbox.TabIndex = 5;
            this.user_textbox.Text = "Username";
            // 
            // host_textbox
            // 
            this.host_textbox.Location = new System.Drawing.Point(141, 174);
            this.host_textbox.Margin = new System.Windows.Forms.Padding(2, 1, 2, 1);
            this.host_textbox.Name = "host_textbox";
            this.host_textbox.Size = new System.Drawing.Size(208, 23);
            this.host_textbox.TabIndex = 4;
            this.host_textbox.Text = "localhost";
            // 
            // password_label
            // 
            this.password_label.AutoSize = true;
            this.password_label.Location = new System.Drawing.Point(73, 241);
            this.password_label.Margin = new System.Windows.Forms.Padding(2, 0, 2, 0);
            this.password_label.Name = "password_label";
            this.password_label.Size = new System.Drawing.Size(60, 15);
            this.password_label.TabIndex = 3;
            this.password_label.Text = "Password:";
            // 
            // user_label
            // 
            this.user_label.AutoSize = true;
            this.user_label.Location = new System.Drawing.Point(70, 209);
            this.user_label.Margin = new System.Windows.Forms.Padding(2, 0, 2, 0);
            this.user_label.Name = "user_label";
            this.user_label.Size = new System.Drawing.Size(63, 15);
            this.user_label.TabIndex = 2;
            this.user_label.Text = "Username:";
            // 
            // host_label
            // 
            this.host_label.AutoSize = true;
            this.host_label.Location = new System.Drawing.Point(98, 177);
            this.host_label.Margin = new System.Windows.Forms.Padding(2, 0, 2, 0);
            this.host_label.Name = "host_label";
            this.host_label.Size = new System.Drawing.Size(35, 15);
            this.host_label.TabIndex = 1;
            this.host_label.Text = "Host:";
            // 
            // dbconn_label
            // 
            this.dbconn_label.AutoSize = true;
            this.dbconn_label.Location = new System.Drawing.Point(65, 145);
            this.dbconn_label.Margin = new System.Windows.Forms.Padding(2, 0, 2, 0);
            this.dbconn_label.Name = "dbconn_label";
            this.dbconn_label.Size = new System.Drawing.Size(187, 15);
            this.dbconn_label.TabIndex = 0;
            this.dbconn_label.Text = "Enter database connection details:";
            // 
            // MainForm
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(7F, 15F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(431, 329);
            this.Controls.Add(this.main_panel);
            this.Margin = new System.Windows.Forms.Padding(2, 1, 2, 1);
            this.Name = "MainForm";
            this.StartPosition = System.Windows.Forms.FormStartPosition.CenterScreen;
            this.Text = "Additional Configuration";
            this.main_panel.ResumeLayout(false);
            this.main_panel.PerformLayout();
            this.ResumeLayout(false);

        }

        #endregion

        private System.Windows.Forms.Panel main_panel;
        private System.Windows.Forms.Label dbconn_label;
        private System.Windows.Forms.Button ok_button;
        private System.Windows.Forms.CheckBox import_checkbox;
        private System.Windows.Forms.TextBox password_textbox;
        private System.Windows.Forms.TextBox user_textbox;
        private System.Windows.Forms.TextBox host_textbox;
        private System.Windows.Forms.Label password_label;
        private System.Windows.Forms.Label user_label;
        private System.Windows.Forms.Label host_label;
        private System.Windows.Forms.Button check_button;
        private System.Windows.Forms.Label info_label;
        private System.Windows.Forms.CheckBox java_checkbox;
    }
}
