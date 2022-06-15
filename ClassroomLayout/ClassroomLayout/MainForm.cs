using System;
using System.Collections.Generic;
using System.Drawing;
using System.IO;
using System.Text;
using System.Windows.Forms;

namespace ClassroomLayout
{
    /// <summary>
    /// Name: Classroom Layout
    /// Purpose: Maintain records of students seating position and teacher details
    /// Author: Tim Wickham
    /// Date: 16 May 2022
    /// Notes: Add, Update, and Delete records saved to CSV and binary format files
    /// </summary>

    public partial class MainForm : Form
    {
        // declare and initialise global variables (not null)
        private List<TableCell> tableCells = new();
        private bool fileLoaded = false;
        private string currentFilePath = string.Empty;
        private bool pauseEvents = false;
        private bool dataChanged = false;
        private readonly int ROWS = 19;
        private readonly int COLS = 10;

        public MainForm()
        {
            InitializeComponent();
            DefaultDeskLayout();
        }

        /// <summary>
        /// Method: DefaultDeskLayout
        /// Purpose: Reads default layout from binary file and fills cells
        /// </summary>
        private void DefaultDeskLayout()
        {
            pauseEvents = true;
            // set rows and columns
            classlayout_table.RowCount = ROWS;
            classlayout_table.ColumnCount = COLS;

            // add numbers to rows and columns
            for (int i = 0; i < classlayout_table.ColumnCount; i++)
            {
                classlayout_table.Columns[i].HeaderText = String.Concat("     ", i.ToString());
            }
            for (int i = 0; i < classlayout_table.RowCount; i++)
            {
                classlayout_table.Rows[i].HeaderCell.Value = i.ToString();
            }
            
            // load default layout from binary file
            string filePath = @"default_layout.dat";
            byte[] byteArray = File.ReadAllBytes(filePath);
            int[] defaultLayout = new int[byteArray.Length];
            Array.Copy(byteArray, defaultLayout, byteArray.Length);

            // draw default layout
            if (defaultLayout != null)
            {
                for (int i = 0; i < defaultLayout.Length - 1; i += 2)
                {
                    classlayout_table.Rows[defaultLayout[i + 1]].
                        Cells[defaultLayout[i]].Style.BackColor = Color.LightBlue;
                }
                // write front desk
                classlayout_table[4, 0].Value = "Front";
                classlayout_table[5, 0].Value = "Desk";
            }
            pauseEvents = false;
        }

        /// <summary>
        /// Method: OpenToolStripMenuItem_Click
        /// Purpose: Action performed when selcting "Open" from file menu
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void OpenToolStripMenuItem_Click(object sender, EventArgs e)
        {
            // check if data changed before opening
            if (dataChanged)
            {
                SaveConfirmDialog();
            }
            OpenCSVFile();
        }

        /// <summary>
        /// Method: OpenCSVFile
        /// Purpose: user selects a file to open, reads in data from file
        /// </summary>
        private void OpenCSVFile()
        {
            // bring up open dialog
            OpenFileDialog openFileDialog = new()
            {
                InitialDirectory = Environment.GetFolderPath(Environment.SpecialFolder.MyDocuments),
                Filter = "CSV files (*.csv)|*.csv",
                FilterIndex = 1,
                RestoreDirectory = true
            };

            if (openFileDialog.ShowDialog() == DialogResult.OK)
            {
                pauseEvents = true;
                ClearData();
                tableCells = new List<TableCell>();
                //Get the path of specified file
                currentFilePath = openFileDialog.FileName;
                // read in file
                using StreamReader fileReader = new(currentFilePath);
                string? line;
                // set line counter to line 1
                int lineCount = 1;
                while ((line = fileReader.ReadLine()) != null)
                {
                    try
                    {
                        string[] lineArray = line.Split(',');
                        // only use cell data after line 4 of csv
                        if (lineCount > 4)
                        {
                            // ensure line has multiple values
                            if (lineArray.Length > 1)
                            {
                                // workaround for old csv format loaded
                                if (lineArray[2] != "BKGRND FILL" && lineArray[2] != "Front Desk")
                                {
                                    // create instance and add line to list
                                    int column = Int16.Parse(lineArray[0]);
                                    int row = Int16.Parse(lineArray[1]);
                                    string name = lineArray[2];
                                    TableCell tableCell = new(column, row, name);
                                    tableCells.Add(tableCell);
                                }
                            }
                        }
                        // process first 4 lines of csv
                        else
                        {
                            switch (lineCount)
                            {
                                case 1: teacher_textbox.Text = lineArray[1]; break;
                                case 2: class_textbox.Text = lineArray[1]; break;
                                case 3: room_textbox.Text = lineArray[1]; break;
                                case 4: date_textbox.Text = lineArray[1]; break;
                            }
                        }
                        fileLoaded = true;
                    }
                    // exception handling
                    catch (FormatException ex)
                    {
                        MessageBox.Show("Error: CSV File not in the correct format" +
                            "\nError Message: " + ex.Message, "Incorrect CSV Format");
                        fileReader.Close();
                        return;
                    }
                    catch (Exception ex)
                    {
                        MessageBox.Show("Error: " + ex.Message, "Error");
                    }
                    lineCount++;
                }
                // close file reader
                fileReader.Close();

                // display data in table
                DisplayCellData();

                pauseEvents = false;
                dataChanged = false;
            }
        }

        /// <summary>
        /// Method: DisplayCellData
        /// Purpose: displays data in table cells
        /// </summary>
        private void DisplayCellData()
        {
            pauseEvents = true;
            // get data from list and display in table
            for (int i = 0; i < tableCells.Count; i++)
            {
                int column = Int16.Parse(tableCells[i].Column.ToString());
                int row = Int16.Parse(tableCells[i].Row.ToString());
                classlayout_table[column, row].Value = tableCells[i].Name.ToString();
            }
            pauseEvents = false;
        }

        /// <summary>
        /// Method: Clear_button_Click
        /// Purpose: action performed when "Clear" button clicked
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void Clear_button_Click(object sender, EventArgs e)
        {
            ClearData();
            if (fileLoaded) dataChanged = true;
            
        }

        /// <summary>
        /// Method: ClearData
        /// Purpose: clears all cells, leaves data in text boxes as per specification
        /// </summary>
        private void ClearData()
        {
            pauseEvents = true;
            // set all cells to empty
            for (int i = 0; i < classlayout_table.Columns.Count; i++)
            {
                for (int j = 0; j < classlayout_table.Rows.Count; j++)
                {
                    classlayout_table[i, j].Value = string.Empty;
                }
            }
            // check if data exists, clear and set dataChanged to true
            if (tableCells.Count > 0)
            {
                dataChanged = true;
                tableCells.Clear();
            }
            // draw the default layout
            DefaultDeskLayout();
            pauseEvents=false;
        }

        /// <summary>
        /// Method: CloseToolStripMenuItem_Click
        /// Purpose: action performed when "Close" selected from file menu
        /// Clears data and sets fileLoaded to false
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void CloseToolStripMenuItem_Click(object sender, EventArgs e)
        {
            // alert if no file loaded
            // TODO: grey out close button until file is loaded
            if (!fileLoaded)
            {
                MessageBox.Show("No file open");
                return;
            }
            // check for data changes and offer to save
            if (dataChanged) SaveConfirmDialog();

            // reset table to default and clear all data
            ClearData();
            teacher_textbox.Text = string.Empty;
            class_textbox.Text = string.Empty;
            room_textbox.Text = string.Empty;
            date_textbox.Text = string.Empty;
            currentFilePath = string.Empty;
            fileLoaded = false;
            dataChanged = false;
        }

        /// <summary>
        /// Method: Sort_button_Click
        /// Purpose: action performed when "Sort" button clicked
        /// sorts students by name and displays
        /// does not interfere with sorting that outputs to csv
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void Sort_button_Click(object sender, EventArgs e)
        {
            // check if there is data to sort
            // TODO: grey out sort button until there is data to sort
            if (tableCells.Count > 0)
            {
                // create new list and sort
                // DO NOT sort global list -- will make csv file sorted by name rather than cells
                List<TableCell> sortedList = new(tableCells);
                sortedList.Sort();
                
                // display results
                string sortedStr = "Name\t\tRow\tCol" +
                    "\n----------------------------------------";
                for (int i = 0; i < sortedList.Count; i++)
                {
                    sortedStr += "\n" + sortedList[i].Name + "\t\t" + sortedList[i].Row + "\t" +
                        sortedList[i].Column;
                }
                MessageBox.Show(sortedStr);
            }
            else
            {
                MessageBox.Show("Nothing to sort");
            }
        }

        /// <summary>
        /// Method: SaveToolStripMenuItem_Click
        /// Purpose: action performed when "Save" selected from file menu
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void SaveToolStripMenuItem_Click(object sender, EventArgs e)
        {
            SaveConfirmDialog();
        }

        /// <summary>
        /// Method: SaveConfirmDialog
        /// Purpose: creates a confirmation dialog for saving
        /// checks if a file is loaded and directs accordingly
        /// </summary>
        private void SaveConfirmDialog()
        {
            // UX: create a dialog relevant to data to save
            // TODO: further assessment and testing of cases
            string dialog;
            if (dataChanged)
            {
                dialog = "Save changes?";
            }
            else if (fileLoaded)
            {
                dialog = "Save current file?" +
                    "\n" + currentFilePath;
            }
            // display dialog if there is nothing to save
            else
            {
                MessageBox.Show("Nothing to save");
                return;
            }

            DialogResult dialogResult = MessageBox.Show(dialog,
                "Save", MessageBoxButtons.YesNo);
            if (dialogResult == DialogResult.Yes)
            {
                BuildListFromCells();
                if (fileLoaded)
                {
                    SaveCSVFile();
                }
                else
                {
                    SaveAsCSVFile();
                }
            }
        }

        /// <summary>
        /// Method: BuildListFromCells
        /// Purpose: reads data in table cells, and creates a list from the items
        /// </summary>
        private void BuildListFromCells()
        {
            // clear current list
            tableCells.Clear();
            // build new list from cells
            for (int i = 0; i < classlayout_table.Columns.Count; i++)
            {
                // skip over 'front desk' row
                for (int j = 1; j < classlayout_table.Rows.Count; j++)
                {
                    if (classlayout_table[i, j].Value != null &&
                        classlayout_table[i, j].Value.ToString() != string.Empty)
                    {
                        TableCell tableCell = new(i, j, classlayout_table[i, j].Value.ToString()!);
                        tableCells.Add(tableCell);
                    }
                }
            }
        }

        /// <summary>
        /// Method: SaveCSVFile
        /// Purpose: writes data to csv file
        /// </summary>
        private void SaveCSVFile()
        {
            try
            {
                if (currentFilePath != null)
                {
                    StreamWriter writer = new(currentFilePath);
                    writer.WriteLine("Teacher:," + teacher_textbox.Text);
                    writer.WriteLine("Class:," + class_textbox.Text);
                    writer.WriteLine("Room:," + room_textbox.Text);
                    writer.WriteLine("Date:," + date_textbox.Text);

                    // write records to csv
                    for (int i = 0; i < tableCells.Count; i++)
                    {
                        writer.WriteLine(tableCells[i].ToCSVString());
                    }
                    writer.Close();
                    MessageBox.Show("Class data saved to " + currentFilePath, "File Saved");
                    dataChanged = false;
                    fileLoaded = true;
                }
            }
            catch (Exception ex)
            {
                MessageBox.Show("Could not save data to " + currentFilePath +
                    "\nError: " + ex.Message, "Save Failed!");
            }
        }

        /// <summary>
        /// Method: SaveAsCSVFile
        /// Purpose: allows user to select file and location to save
        /// </summary>
        private void SaveAsCSVFile()
        {
            SaveFileDialog saveFileDialog = new()
            {
                InitialDirectory = Environment.GetFolderPath(Environment.SpecialFolder.MyDocuments),
                Filter = "CSV files (*.csv)|*.csv",
                FilterIndex = 1,
                RestoreDirectory = true
            };
            if (saveFileDialog.ShowDialog() == DialogResult.OK)
            {
                currentFilePath = saveFileDialog.FileName;
                SaveCSVFile();
            }
        }

        /// <summary>
        /// Method: Savechange_button_Click
        /// Purpose: action performed when "Save Changes" button clicked
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void Savechange_button_Click(object sender, EventArgs e)
        {
            if (dataChanged)
            {
                SaveConfirmDialog();
            }
            else
            {
                MessageBox.Show("No changes. Nothing to save");
            }
        }

        /// <summary>
        /// Method: SaveAsToolStripMenuItem_Click
        /// Purpose: action performed when "Save As" selected from file menu
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void SaveAsToolStripMenuItem_Click(object sender, EventArgs e)
        {
            SaveAsCSVFile();
        }

        /// <summary>
        /// Method: Classlayout_table_CellValueChanged
        /// Purpose: refreshes data in list when cell changed
        /// sets dataChanged to true
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void Classlayout_table_CellValueChanged(object sender, DataGridViewCellEventArgs e)
        {
            if (!pauseEvents)
            {
                BuildListFromCells();
                dataChanged = true;
            }
        }

        /// <summary>
        /// Method: Teacher_textbox_TextChanged
        /// Purpose: detects teacher data change
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void Teacher_textbox_TextChanged(object sender, EventArgs e)
        {
            if (!pauseEvents) dataChanged = true;
        }

        /// <summary>
        /// Method: Class_textbox_TextChanged
        /// Purpose: detects class data change
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void Class_textbox_TextChanged(object sender, EventArgs e)
        {
            if (!pauseEvents) dataChanged = true;
        }

        /// <summary>
        /// Method: Room_textbox_TextChanged
        /// Purpose: detects room data change
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void Room_textbox_TextChanged(object sender, EventArgs e)
        {
            if (!pauseEvents) dataChanged = true;
        }

        /// <summary>
        /// Method: Date_textbox_TextChanged
        /// Purpose: detects date data change
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void Date_textbox_TextChanged(object sender, EventArgs e)
        {
            if (!pauseEvents) dataChanged = true;
        }

        /// <summary>
        /// Method: Exit_button_Click
        /// Purpose: action performed when "Exit" button clicked
        /// show save confirmation if data changed
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void Exit_button_Click(object sender, EventArgs e)
        {
            if (dataChanged) SaveConfirmDialog();
            ExitApplication();
        }

        /// <summary>
        /// Method: ExitApplication
        /// Purpose: exit application
        /// </summary>
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

        /// <summary>
        /// Method: ExitToolStripMenuItem_Click
        /// Purpose: action performed when "Exit" selected from file menu
        /// show save confirmation if data changed
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void ExitToolStripMenuItem_Click(object sender, EventArgs e)
        {
            if (dataChanged) SaveConfirmDialog();
            ExitApplication();
        }

        /// <summary>
        /// Method: Search_button_Click
        /// Purpose: action performed when "Search" button clicked
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void Search_button_Click(object sender, EventArgs e)
        {
            SearchName();
        }

        /// <summary>
        /// Method: SearchName
        /// Purpose: case insensitive binary search for name 
        /// </summary>
        private void SearchName()
        {
            if (tableCells.Count > 0 && search_textbox.Text != string.Empty)
            {
                List<TableCell> sortedList = new(tableCells);
                sortedList.Sort();

                bool foundStatus = false;
                // set first value to 0
                int first = 0;
                // set last value as number of recyclers
                int last = tableCells.Count - 1;
                // declare mid variable
                int mid;
                // set found position as -1 (not found)
                int posFound = -1;

                while (!foundStatus && first <= last)
                {
                    mid = (first + last) / 2;

                    // determine if name is potentially in lower half of list 
                    if (search_textbox.Text.ToLower().CompareTo(sortedList[mid].Name.ToLower()) < 0)
                    {
                        last = mid - 1;
                    }
                    // determine if name is potentially in upper half of list
                    else if (search_textbox.Text.ToLower().CompareTo(sortedList[mid].Name.ToLower()) > 0)
                    {
                        first = mid + 1;
                    }
                    // if comparison = 0 then found
                    else
                    {
                        foundStatus = true;
                        posFound = mid;
                    }
                }
                // check if found
                if (posFound == -1)
                {
                    // display message if not found
                    MessageBox.Show("Not found");
                }
                else
                {
                    // displays sorted list, indicating found entry
                    string sortedStr = "Name\t\tRow\tCol" +
                    "\n----------------------------------------";
                    for (int i = 0; i < sortedList.Count; i++)
                    {
                        sortedStr += "\n" + sortedList[i].Name + "\t\t" + sortedList[i].Row + "\t" +
                            sortedList[i].Column;
                        if (i == posFound)
                        {
                            sortedStr += " <=== Found";
                            // highlight cell of found entry
                            classlayout_table.CurrentCell = classlayout_table.Rows[sortedList[i].Row].Cells[sortedList[i].Column];
                        }
                    }
                    search_textbox.Text = string.Empty;
                    MessageBox.Show(sortedStr);
                }
            }
            else
            {
                MessageBox.Show("Nothing to search");
            }
        }

        /// <summary>
        /// Method: Search_textbox_KeyPress
        /// Purpose: detects if "Enter" key is pressed in search box
        /// runs search if detected
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="key"></param>
        private void Search_textbox_KeyPress(object sender, KeyPressEventArgs key)
        {
            if (key.KeyChar.Equals('\r'))
            {
                SearchName();
            }
        }

        /// <summary>
        /// Method: Saveraf_button_Click
        /// Purpose: saves data to binary format file when "Save RAF" button clicked
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void Saveraf_button_Click(object sender, EventArgs e)
        {
            if (dataChanged)
            {
                SaveFileDialog saveFileDialog = new()
                {
                    InitialDirectory = Environment.GetFolderPath(Environment.SpecialFolder.MyDocuments),
                    Filter = "RAF files (*.raf)|*.raf",
                    FilterIndex = 1,
                    RestoreDirectory = true
                };

                if (saveFileDialog.ShowDialog() == DialogResult.OK)
                {
                    string filePath = saveFileDialog.FileName;
                    try
                    {
                        // write data in binary format
                        using (var stream = File.Open(filePath, FileMode.Create))
                        {
                            using var writer = new BinaryWriter(stream, Encoding.UTF8, false);
                            writer.Write(teacher_textbox.Text);
                            writer.Write(class_textbox.Text);
                            writer.Write(room_textbox.Text);
                            writer.Write(date_textbox.Text);
                            for (int i = 0; i < tableCells.Count; i++)
                            {
                                writer.Write(tableCells[i].ToCSVString());
                            }
                        }

                        MessageBox.Show("Data successfully written to random access file " + filePath, "SUCCESS - Random Access File written!");
                    }

                    catch (Exception ex)
                    {
                        MessageBox.Show("ERROR: Data NOT written to random access file " + filePath +
                            "\n" + ex.Message + "\n" + ex.StackTrace, "ERROR - Problem in writing to random access file");
                    }
                }
            }
        }

        /// <summary>
        /// Method: OpenRAFToolStripMenuItem_Click
        /// Purpose: reads in binary format file when "Open RAF" selected from file menu
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void OpenRAFToolStripMenuItem_Click(object sender, EventArgs e)
        {
            OpenFileDialog openFileDialog = new()
            {
                InitialDirectory = Environment.GetFolderPath(Environment.SpecialFolder.MyDocuments),
                Filter = "RAF files (*.raf)|*.raf",
                FilterIndex = 1,
                RestoreDirectory = true
            };

            if (openFileDialog.ShowDialog() == DialogResult.OK)
            {
                ClearData();
                string filePath = openFileDialog.FileName;
                try
                {
                    // read data from binary format file
                    using (var stream = File.Open(filePath, FileMode.Open))
                    {
                        using var reader = new BinaryReader(stream, Encoding.UTF8, false);
                        int counter = 1;
                        while (reader.BaseStream.Position != reader.BaseStream.Length)
                        {
                            // read cell data
                            if (counter > 4)
                            {
                                string tempStr = reader.ReadString();
                                string[] tempArray = tempStr.Split(',');
                                int column = Int16.Parse(tempArray[0]);
                                int row = Int16.Parse(tempArray[1]);
                                string name = tempArray[2];
                                TableCell tableCell = new(column, row, name);
                                tableCells.Add(tableCell);
                            }
                            else
                            {
                                // read text box data
                                switch (counter)
                                {
                                    case 1: teacher_textbox.Text = reader.ReadString(); break;
                                    case 2: class_textbox.Text = reader.ReadString(); break;
                                    case 3: room_textbox.Text = reader.ReadString(); break;
                                    case 4: date_textbox.Text = reader.ReadString(); break;
                                }
                            }
                            counter++;
                        }
                    }
                    pauseEvents = true;
                    DisplayCellData();
                    pauseEvents = false;
                    MessageBox.Show("Data successfully read from random access file " + filePath, "SUCCESS - Random Access File read!");
                }

                catch (Exception ex)
                {
                    MessageBox.Show("ERROR: Data NOT read from random access file " + filePath +
                        "\n" + ex.Message + "\n" + ex.StackTrace, "ERROR - Problem in reading to random access file");
                }
            }
        }
    }
}