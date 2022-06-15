namespace ClassroomLayout
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
            System.Windows.Forms.DataGridViewCellStyle dataGridViewCellStyle1 = new System.Windows.Forms.DataGridViewCellStyle();
            System.Windows.Forms.DataGridViewCellStyle dataGridViewCellStyle2 = new System.Windows.Forms.DataGridViewCellStyle();
            System.Windows.Forms.DataGridViewCellStyle dataGridViewCellStyle3 = new System.Windows.Forms.DataGridViewCellStyle();
            this.menu_panel = new System.Windows.Forms.Panel();
            this.menuStrip = new System.Windows.Forms.MenuStrip();
            this.fileToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.openToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.openRAFToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.saveToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.saveAsToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.closeToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.exitToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.info_panel = new System.Windows.Forms.Panel();
            this.date_textbox = new System.Windows.Forms.TextBox();
            this.date_label = new System.Windows.Forms.Label();
            this.room_textbox = new System.Windows.Forms.TextBox();
            this.room_label = new System.Windows.Forms.Label();
            this.class_textbox = new System.Windows.Forms.TextBox();
            this.class_label = new System.Windows.Forms.Label();
            this.teacher_textbox = new System.Windows.Forms.TextBox();
            this.teacher_label = new System.Windows.Forms.Label();
            this.classlayout_panel = new System.Windows.Forms.Panel();
            this.classlayout_table = new System.Windows.Forms.DataGridView();
            this.control_panel = new System.Windows.Forms.Panel();
            this.search_label = new System.Windows.Forms.Label();
            this.exit_button = new System.Windows.Forms.Button();
            this.saveraf_button = new System.Windows.Forms.Button();
            this.search_button = new System.Windows.Forms.Button();
            this.search_textbox = new System.Windows.Forms.TextBox();
            this.sort_button = new System.Windows.Forms.Button();
            this.savechange_button = new System.Windows.Forms.Button();
            this.clear_button = new System.Windows.Forms.Button();
            this.menu_panel.SuspendLayout();
            this.menuStrip.SuspendLayout();
            this.info_panel.SuspendLayout();
            this.classlayout_panel.SuspendLayout();
            ((System.ComponentModel.ISupportInitialize)(this.classlayout_table)).BeginInit();
            this.control_panel.SuspendLayout();
            this.SuspendLayout();
            // 
            // menu_panel
            // 
            this.menu_panel.Controls.Add(this.menuStrip);
            this.menu_panel.Location = new System.Drawing.Point(1, 2);
            this.menu_panel.Name = "menu_panel";
            this.menu_panel.Size = new System.Drawing.Size(800, 23);
            this.menu_panel.TabIndex = 0;
            // 
            // menuStrip
            // 
            this.menuStrip.ImageScalingSize = new System.Drawing.Size(32, 32);
            this.menuStrip.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.fileToolStripMenuItem});
            this.menuStrip.Location = new System.Drawing.Point(0, 0);
            this.menuStrip.Name = "menuStrip";
            this.menuStrip.Size = new System.Drawing.Size(800, 24);
            this.menuStrip.TabIndex = 0;
            this.menuStrip.Text = "menuStrip";
            // 
            // fileToolStripMenuItem
            // 
            this.fileToolStripMenuItem.DropDownItems.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.openToolStripMenuItem,
            this.openRAFToolStripMenuItem,
            this.saveToolStripMenuItem,
            this.saveAsToolStripMenuItem,
            this.closeToolStripMenuItem,
            this.exitToolStripMenuItem});
            this.fileToolStripMenuItem.Name = "fileToolStripMenuItem";
            this.fileToolStripMenuItem.Size = new System.Drawing.Size(37, 20);
            this.fileToolStripMenuItem.Text = "File";
            // 
            // openToolStripMenuItem
            // 
            this.openToolStripMenuItem.Name = "openToolStripMenuItem";
            this.openToolStripMenuItem.Size = new System.Drawing.Size(127, 22);
            this.openToolStripMenuItem.Text = "Open";
            this.openToolStripMenuItem.Click += new System.EventHandler(this.OpenToolStripMenuItem_Click);
            // 
            // openRAFToolStripMenuItem
            // 
            this.openRAFToolStripMenuItem.Name = "openRAFToolStripMenuItem";
            this.openRAFToolStripMenuItem.Size = new System.Drawing.Size(127, 22);
            this.openRAFToolStripMenuItem.Text = "Open RAF";
            this.openRAFToolStripMenuItem.Click += new System.EventHandler(this.OpenRAFToolStripMenuItem_Click);
            // 
            // saveToolStripMenuItem
            // 
            this.saveToolStripMenuItem.Name = "saveToolStripMenuItem";
            this.saveToolStripMenuItem.Size = new System.Drawing.Size(127, 22);
            this.saveToolStripMenuItem.Text = "Save";
            this.saveToolStripMenuItem.Click += new System.EventHandler(this.SaveToolStripMenuItem_Click);
            // 
            // saveAsToolStripMenuItem
            // 
            this.saveAsToolStripMenuItem.Name = "saveAsToolStripMenuItem";
            this.saveAsToolStripMenuItem.Size = new System.Drawing.Size(127, 22);
            this.saveAsToolStripMenuItem.Text = "Save As...";
            this.saveAsToolStripMenuItem.Click += new System.EventHandler(this.SaveAsToolStripMenuItem_Click);
            // 
            // closeToolStripMenuItem
            // 
            this.closeToolStripMenuItem.Name = "closeToolStripMenuItem";
            this.closeToolStripMenuItem.Size = new System.Drawing.Size(127, 22);
            this.closeToolStripMenuItem.Text = "Close";
            this.closeToolStripMenuItem.Click += new System.EventHandler(this.CloseToolStripMenuItem_Click);
            // 
            // exitToolStripMenuItem
            // 
            this.exitToolStripMenuItem.Name = "exitToolStripMenuItem";
            this.exitToolStripMenuItem.Size = new System.Drawing.Size(127, 22);
            this.exitToolStripMenuItem.Text = "Exit";
            this.exitToolStripMenuItem.Click += new System.EventHandler(this.ExitToolStripMenuItem_Click);
            // 
            // info_panel
            // 
            this.info_panel.BackColor = System.Drawing.Color.LightSteelBlue;
            this.info_panel.Controls.Add(this.date_textbox);
            this.info_panel.Controls.Add(this.date_label);
            this.info_panel.Controls.Add(this.room_textbox);
            this.info_panel.Controls.Add(this.room_label);
            this.info_panel.Controls.Add(this.class_textbox);
            this.info_panel.Controls.Add(this.class_label);
            this.info_panel.Controls.Add(this.teacher_textbox);
            this.info_panel.Controls.Add(this.teacher_label);
            this.info_panel.Location = new System.Drawing.Point(12, 29);
            this.info_panel.Name = "info_panel";
            this.info_panel.Size = new System.Drawing.Size(812, 34);
            this.info_panel.TabIndex = 1;
            // 
            // date_textbox
            // 
            this.date_textbox.Location = new System.Drawing.Point(679, 6);
            this.date_textbox.Name = "date_textbox";
            this.date_textbox.Size = new System.Drawing.Size(100, 23);
            this.date_textbox.TabIndex = 7;
            this.date_textbox.TextChanged += new System.EventHandler(this.Date_textbox_TextChanged);
            // 
            // date_label
            // 
            this.date_label.AutoSize = true;
            this.date_label.Location = new System.Drawing.Point(646, 9);
            this.date_label.Name = "date_label";
            this.date_label.Size = new System.Drawing.Size(31, 15);
            this.date_label.TabIndex = 6;
            this.date_label.Text = "Date";
            // 
            // room_textbox
            // 
            this.room_textbox.Location = new System.Drawing.Point(455, 6);
            this.room_textbox.Name = "room_textbox";
            this.room_textbox.Size = new System.Drawing.Size(51, 23);
            this.room_textbox.TabIndex = 5;
            this.room_textbox.TextChanged += new System.EventHandler(this.Room_textbox_TextChanged);
            // 
            // room_label
            // 
            this.room_label.AutoSize = true;
            this.room_label.Location = new System.Drawing.Point(410, 9);
            this.room_label.Name = "room_label";
            this.room_label.Size = new System.Drawing.Size(39, 15);
            this.room_label.TabIndex = 4;
            this.room_label.Text = "Room";
            // 
            // class_textbox
            // 
            this.class_textbox.Location = new System.Drawing.Point(350, 6);
            this.class_textbox.Name = "class_textbox";
            this.class_textbox.Size = new System.Drawing.Size(51, 23);
            this.class_textbox.TabIndex = 3;
            this.class_textbox.TextChanged += new System.EventHandler(this.Class_textbox_TextChanged);
            // 
            // class_label
            // 
            this.class_label.AutoSize = true;
            this.class_label.Location = new System.Drawing.Point(310, 9);
            this.class_label.Name = "class_label";
            this.class_label.Size = new System.Drawing.Size(34, 15);
            this.class_label.TabIndex = 2;
            this.class_label.Text = "Class";
            // 
            // teacher_textbox
            // 
            this.teacher_textbox.Location = new System.Drawing.Point(88, 6);
            this.teacher_textbox.Name = "teacher_textbox";
            this.teacher_textbox.Size = new System.Drawing.Size(100, 23);
            this.teacher_textbox.TabIndex = 1;
            this.teacher_textbox.TextChanged += new System.EventHandler(this.Teacher_textbox_TextChanged);
            // 
            // teacher_label
            // 
            this.teacher_label.AutoSize = true;
            this.teacher_label.Location = new System.Drawing.Point(33, 9);
            this.teacher_label.Name = "teacher_label";
            this.teacher_label.Size = new System.Drawing.Size(47, 15);
            this.teacher_label.TabIndex = 0;
            this.teacher_label.Text = "Teacher";
            // 
            // classlayout_panel
            // 
            this.classlayout_panel.Controls.Add(this.classlayout_table);
            this.classlayout_panel.Location = new System.Drawing.Point(12, 68);
            this.classlayout_panel.Name = "classlayout_panel";
            this.classlayout_panel.Size = new System.Drawing.Size(815, 411);
            this.classlayout_panel.TabIndex = 2;
            // 
            // classlayout_table
            // 
            this.classlayout_table.AllowUserToAddRows = false;
            this.classlayout_table.AllowUserToDeleteRows = false;
            this.classlayout_table.AllowUserToResizeColumns = false;
            this.classlayout_table.AllowUserToResizeRows = false;
            this.classlayout_table.AutoSizeColumnsMode = System.Windows.Forms.DataGridViewAutoSizeColumnsMode.Fill;
            this.classlayout_table.AutoSizeRowsMode = System.Windows.Forms.DataGridViewAutoSizeRowsMode.AllCells;
            dataGridViewCellStyle1.Alignment = System.Windows.Forms.DataGridViewContentAlignment.BottomCenter;
            dataGridViewCellStyle1.BackColor = System.Drawing.SystemColors.Control;
            dataGridViewCellStyle1.Font = new System.Drawing.Font("Segoe UI", 9F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point);
            dataGridViewCellStyle1.ForeColor = System.Drawing.SystemColors.WindowText;
            dataGridViewCellStyle1.SelectionBackColor = System.Drawing.SystemColors.Highlight;
            dataGridViewCellStyle1.SelectionForeColor = System.Drawing.SystemColors.HighlightText;
            dataGridViewCellStyle1.WrapMode = System.Windows.Forms.DataGridViewTriState.True;
            this.classlayout_table.ColumnHeadersDefaultCellStyle = dataGridViewCellStyle1;
            this.classlayout_table.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
            dataGridViewCellStyle2.Alignment = System.Windows.Forms.DataGridViewContentAlignment.MiddleCenter;
            dataGridViewCellStyle2.BackColor = System.Drawing.SystemColors.Window;
            dataGridViewCellStyle2.Font = new System.Drawing.Font("Segoe UI", 9F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point);
            dataGridViewCellStyle2.ForeColor = System.Drawing.SystemColors.ControlText;
            dataGridViewCellStyle2.SelectionBackColor = System.Drawing.SystemColors.Highlight;
            dataGridViewCellStyle2.SelectionForeColor = System.Drawing.SystemColors.HighlightText;
            dataGridViewCellStyle2.WrapMode = System.Windows.Forms.DataGridViewTriState.False;
            this.classlayout_table.DefaultCellStyle = dataGridViewCellStyle2;
            this.classlayout_table.EditMode = System.Windows.Forms.DataGridViewEditMode.EditOnEnter;
            this.classlayout_table.Location = new System.Drawing.Point(3, 3);
            this.classlayout_table.Name = "classlayout_table";
            dataGridViewCellStyle3.Alignment = System.Windows.Forms.DataGridViewContentAlignment.MiddleRight;
            dataGridViewCellStyle3.BackColor = System.Drawing.SystemColors.Control;
            dataGridViewCellStyle3.Font = new System.Drawing.Font("Segoe UI", 9F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point);
            dataGridViewCellStyle3.ForeColor = System.Drawing.SystemColors.WindowText;
            dataGridViewCellStyle3.SelectionBackColor = System.Drawing.SystemColors.Highlight;
            dataGridViewCellStyle3.SelectionForeColor = System.Drawing.SystemColors.HighlightText;
            dataGridViewCellStyle3.WrapMode = System.Windows.Forms.DataGridViewTriState.True;
            this.classlayout_table.RowHeadersDefaultCellStyle = dataGridViewCellStyle3;
            this.classlayout_table.RowHeadersWidthSizeMode = System.Windows.Forms.DataGridViewRowHeadersWidthSizeMode.AutoSizeToAllHeaders;
            this.classlayout_table.RowTemplate.Height = 17;
            this.classlayout_table.RowTemplate.Resizable = System.Windows.Forms.DataGridViewTriState.False;
            this.classlayout_table.SelectionMode = System.Windows.Forms.DataGridViewSelectionMode.CellSelect;
            this.classlayout_table.ShowEditingIcon = false;
            this.classlayout_table.Size = new System.Drawing.Size(809, 405);
            this.classlayout_table.TabIndex = 0;
            this.classlayout_table.CellValueChanged += new System.Windows.Forms.DataGridViewCellEventHandler(this.Classlayout_table_CellValueChanged);
            // 
            // control_panel
            // 
            this.control_panel.BackColor = System.Drawing.Color.LightSteelBlue;
            this.control_panel.Controls.Add(this.search_label);
            this.control_panel.Controls.Add(this.exit_button);
            this.control_panel.Controls.Add(this.saveraf_button);
            this.control_panel.Controls.Add(this.search_button);
            this.control_panel.Controls.Add(this.search_textbox);
            this.control_panel.Controls.Add(this.sort_button);
            this.control_panel.Controls.Add(this.savechange_button);
            this.control_panel.Controls.Add(this.clear_button);
            this.control_panel.Location = new System.Drawing.Point(12, 482);
            this.control_panel.Name = "control_panel";
            this.control_panel.Size = new System.Drawing.Size(812, 54);
            this.control_panel.TabIndex = 3;
            // 
            // search_label
            // 
            this.search_label.AutoSize = true;
            this.search_label.Location = new System.Drawing.Point(368, 6);
            this.search_label.Name = "search_label";
            this.search_label.Size = new System.Drawing.Size(77, 15);
            this.search_label.TabIndex = 7;
            this.search_label.Text = "Search Name";
            // 
            // exit_button
            // 
            this.exit_button.Location = new System.Drawing.Point(685, 22);
            this.exit_button.Name = "exit_button";
            this.exit_button.Size = new System.Drawing.Size(88, 23);
            this.exit_button.TabIndex = 6;
            this.exit_button.Text = "Exit";
            this.exit_button.UseVisualStyleBackColor = true;
            this.exit_button.Click += new System.EventHandler(this.Exit_button_Click);
            // 
            // saveraf_button
            // 
            this.saveraf_button.Location = new System.Drawing.Point(589, 23);
            this.saveraf_button.Name = "saveraf_button";
            this.saveraf_button.Size = new System.Drawing.Size(88, 23);
            this.saveraf_button.TabIndex = 5;
            this.saveraf_button.Text = "Save RAF";
            this.saveraf_button.UseVisualStyleBackColor = true;
            this.saveraf_button.Click += new System.EventHandler(this.Saveraf_button_Click);
            // 
            // search_button
            // 
            this.search_button.Location = new System.Drawing.Point(493, 23);
            this.search_button.Name = "search_button";
            this.search_button.Size = new System.Drawing.Size(88, 23);
            this.search_button.TabIndex = 4;
            this.search_button.Text = "Search";
            this.search_button.UseVisualStyleBackColor = true;
            this.search_button.Click += new System.EventHandler(this.Search_button_Click);
            // 
            // search_textbox
            // 
            this.search_textbox.Location = new System.Drawing.Point(324, 24);
            this.search_textbox.Name = "search_textbox";
            this.search_textbox.Size = new System.Drawing.Size(163, 23);
            this.search_textbox.TabIndex = 3;
            this.search_textbox.KeyPress += new System.Windows.Forms.KeyPressEventHandler(this.Search_textbox_KeyPress);
            // 
            // sort_button
            // 
            this.sort_button.Location = new System.Drawing.Point(230, 22);
            this.sort_button.Name = "sort_button";
            this.sort_button.Size = new System.Drawing.Size(88, 23);
            this.sort_button.TabIndex = 2;
            this.sort_button.Text = "Show Sorted";
            this.sort_button.UseVisualStyleBackColor = true;
            this.sort_button.Click += new System.EventHandler(this.Sort_button_Click);
            // 
            // savechange_button
            // 
            this.savechange_button.Location = new System.Drawing.Point(135, 22);
            this.savechange_button.Name = "savechange_button";
            this.savechange_button.Size = new System.Drawing.Size(88, 23);
            this.savechange_button.TabIndex = 1;
            this.savechange_button.Text = "Save Changes";
            this.savechange_button.UseVisualStyleBackColor = true;
            this.savechange_button.Click += new System.EventHandler(this.Savechange_button_Click);
            // 
            // clear_button
            // 
            this.clear_button.Location = new System.Drawing.Point(40, 22);
            this.clear_button.Name = "clear_button";
            this.clear_button.Size = new System.Drawing.Size(88, 23);
            this.clear_button.TabIndex = 0;
            this.clear_button.Text = "Clear";
            this.clear_button.UseVisualStyleBackColor = true;
            this.clear_button.Click += new System.EventHandler(this.Clear_button_Click);
            // 
            // MainForm
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(7F, 15F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(839, 550);
            this.Controls.Add(this.control_panel);
            this.Controls.Add(this.classlayout_panel);
            this.Controls.Add(this.info_panel);
            this.Controls.Add(this.menu_panel);
            this.MainMenuStrip = this.menuStrip;
            this.Name = "MainForm";
            this.StartPosition = System.Windows.Forms.FormStartPosition.CenterScreen;
            this.Text = "Classroom Layout";
            this.menu_panel.ResumeLayout(false);
            this.menu_panel.PerformLayout();
            this.menuStrip.ResumeLayout(false);
            this.menuStrip.PerformLayout();
            this.info_panel.ResumeLayout(false);
            this.info_panel.PerformLayout();
            this.classlayout_panel.ResumeLayout(false);
            ((System.ComponentModel.ISupportInitialize)(this.classlayout_table)).EndInit();
            this.control_panel.ResumeLayout(false);
            this.control_panel.PerformLayout();
            this.ResumeLayout(false);

        }

        #endregion

        private System.Windows.Forms.Panel menu_panel;
        private System.Windows.Forms.MenuStrip menuStrip;
        private System.Windows.Forms.ToolStripMenuItem fileToolStripMenuItem;
        private System.Windows.Forms.ToolStripMenuItem openToolStripMenuItem;
        private System.Windows.Forms.ToolStripMenuItem saveToolStripMenuItem;
        private System.Windows.Forms.ToolStripMenuItem saveAsToolStripMenuItem;
        private System.Windows.Forms.ToolStripMenuItem exitToolStripMenuItem;
        private System.Windows.Forms.Panel info_panel;
        private System.Windows.Forms.TextBox date_textbox;
        private System.Windows.Forms.Label date_label;
        private System.Windows.Forms.TextBox room_textbox;
        private System.Windows.Forms.Label room_label;
        private System.Windows.Forms.TextBox class_textbox;
        private System.Windows.Forms.Label class_label;
        private System.Windows.Forms.TextBox teacher_textbox;
        private System.Windows.Forms.Label teacher_label;
        private System.Windows.Forms.Panel classlayout_panel;
        private System.Windows.Forms.Panel control_panel;
        private System.Windows.Forms.Label search_label;
        private System.Windows.Forms.Button exit_button;
        private System.Windows.Forms.Button saveraf_button;
        private System.Windows.Forms.Button search_button;
        private System.Windows.Forms.TextBox search_textbox;
        private System.Windows.Forms.Button sort_button;
        private System.Windows.Forms.Button savechange_button;
        private System.Windows.Forms.Button clear_button;
        private System.Windows.Forms.DataGridView classlayout_table;
        private System.Windows.Forms.ToolStripMenuItem closeToolStripMenuItem;
        private System.Windows.Forms.ToolStripMenuItem openRAFToolStripMenuItem;
    }
}
