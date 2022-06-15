using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.IO;
using System.Windows.Forms;

namespace LocalRecyclers
{
    /// <summary>
    /// Name: Local Recyclers
    /// Purpose: Maintain records of local recyclers
    /// Author: Tim Wickham
    /// Date: 16 May 2022
    /// Notes: Add, Update, and Delete records saved to CSV file.
    /// </summary>
    public partial class MainForm : Form
    {
        /// <summary>
        /// Declare variables:
        /// - Recycler List from Recycler class
        /// - Recycled Materials List used for "Filter By"
        /// - Current Record to show in text boxes
        /// </summary>
        private List<Recycler> recyclerList;
        private List<string> recycledMaterials;
        private int currentRecord;

        /// <summary>
        /// Method: Main Form method
        /// Purpose: Initialises components and calls methods
        /// </summary>
        public MainForm()
        {
            // Initialise Form Components
            InitializeComponent();

            // Initialise recycler list from recycler class
            recyclerList = new List<Recycler>();

            // read csv data into recycler list
            ReadRecyclerData();

            // sort recycler list alphabetically by name
            recyclerList.Sort();

            // select the first record
            currentRecord = 0;

            // display data in text boxes
            DisplayCurrentRecord();

            // generate recycled material list
            CreateMaterialList();

            //display data in recycler listing
            DisplayRecyclerData();
        }

        /// <summary>
        /// Method: ReadRecyclerData()
        /// Purpose: Read in CSV file containing recycler data
        /// </summary>
        public void ReadRecyclerData()
        {
            // set path and filename of recycler CSV
            string filePath = @"recyclers.csv";

            try
            {
                // check if file exists
                if (File.Exists(filePath))
                {
                    using (StreamReader fileReader = new StreamReader(filePath))
                    {
                        // read line by line, adding each entry to Recycler List
                        string line;
                        while ((line = fileReader.ReadLine()) != null)
                        {
                            string[] lineArray = line.Split(',');
                            string name = lineArray[0];
                            string address = lineArray[1];
                            string phone = lineArray[2];
                            string website = lineArray[3];
                            string recycles = lineArray[4];
                            Recycler recycler = new Recycler(name, address, phone, website, recycles);
                            recyclerList.Add(recycler);
                        }
                        // close file reader
                        fileReader.Close();
                    }
                }
                else
                {
                    // if file doesn't exist
                    // display error and close program
                    // Todo: implement create file rather than close app
                    MessageBox.Show("File does not exist");
                    if (System.Windows.Forms.Application.MessageLoop)
                    {
                        // WinForms app
                        System.Windows.Forms.Application.Exit();
                    }
                    else
                    {
                        // console app
                        System.Environment.Exit(1);
                    }
                }
            }
            catch (IOException ex)
            {
                MessageBox.Show(ex.Message);
            }
            catch (Exception ex)
            {
                MessageBox.Show(ex.Message);
            }
        }

        /// <summary>
        /// Method: DisplayCurrentRecord()
        /// Purpose: Displays the currently selected record data in text boxes
        /// </summary>
        public void DisplayCurrentRecord()
        {
            // checks if data exits
            if (recyclerList.Count > 0)
            {
                // displays currently selected record in text boxes
                bname_textbox.Text = recyclerList[currentRecord].Name;
                address_textbox.Text = recyclerList[currentRecord].Address;
                phone_textbox.Text = recyclerList[currentRecord].Phone;
                website_textbox.Text = recyclerList[currentRecord].Website;
                // splits material recycled data by delimiter " - " 
                string[] recyclesArray = recyclerList[currentRecord].Recycles.Split(new string[] { " - " }, StringSplitOptions.None);
                string recyclesString = "";
                for (int i = 0; i < recyclesArray.Length; i++)
                {
                    recyclesString += recyclesArray[i].ToString() + "\r\n";
                }
                recycles_textbox.Text = recyclesString;
            }
            else
            {
                // display nothing in text boxes
                bname_textbox.Text = string.Empty;
                address_textbox.Text = string.Empty;
                phone_textbox.Text = string.Empty;
                website_textbox.Text = string.Empty;
                recycles_textbox.Text = string.Empty;
            }
        }

        /// <summary>
        /// Method: CreateMaterialList()
        /// Purpose: Extract recycled materials from recyclers and create list
        /// Add individual elements to 'filter by' combobox
        /// </summary>
        public void CreateMaterialList()
        {
            // create a new list
            recycledMaterials = new List<string>();
            // clear combobox
            filterby_combobox.Items.Clear();
            // add item "no filter"
            filterby_combobox.Items.Add("No filter");

            // extract all 'recycled materials' from recyclers and add to recycledMaterials list
            for (int i = 0; i < recyclerList.Count; i++)
            {
                string[] recyclesArray = recyclerList[i].Recycles.Split(new string[] { " - " }, StringSplitOptions.None);
                for (int j = 0; j < recyclesArray.Length; j++)
                {
                    if (!string.IsNullOrEmpty(recyclesArray[j]))
                    {
                        recycledMaterials.Add(recyclesArray[j]);
                    }
                }
            }
            // sort recycled materials list
            recycledMaterials.Sort();

            // check for duplicates and remove
            for (int i = 0; i < recycledMaterials.Count - 1; i++)
            {
                if (recycledMaterials[i].ToString() == recycledMaterials[i + 1].ToString())
                {
                    recycledMaterials.RemoveAt(i);
                    // step back to check again
                    i--;
                }
            }

            // add list to 'filter by' combobox
            for (int i = 0; i < recycledMaterials.Count; i++)
            {
                filterby_combobox.Items.Add(recycledMaterials[i].ToString());
            }

            // select 'no filter' option
            filterby_combobox.SelectedIndex = 0;
        }

        /// <summary>
        /// Method: DisplayRecyclerData()
        /// Purpose: Show list of recyclers in recycler listing area
        /// </summary>
        public void DisplayRecyclerData()
        {
            // create column labels and horizontal line
            string recyclerData = "Name" + "\t\t\t" + "Phone" + "\t\t" +
                "Website" + "\r\n";
            recyclerData += "-----------------------------------------";
            recyclerData += "-----------------------------------------\r\n";

            // if no filter selected, display all recyclers
            if (filterby_combobox.SelectedIndex == 0)
            {
                for (int i = 0; i < recyclerList.Count; i++)
                {
                    recyclerData += recyclerList[i].ToString() + "\r\n";
                }
            }
            // if filter selected, find and display matching recyclers
            else
            {
                for (int i = 0; i < recyclerList.Count; i++)
                {
                    string[] recyclesArray = recyclerList[i].Recycles.Split(new string[] { " - " }, StringSplitOptions.None);
                    for (int j = 0; j < recyclesArray.Length; j++)
                    {
                        // if match found, add to listing and exit loop
                        if (recyclesArray[j].ToString().Equals(filterby_combobox.SelectedItem.ToString()))
                        {
                            recyclerData += recyclerList[i].ToString() + "\r\n";
                            break;
                        }
                    }
                }
            }
            // display listing
            listing_textbox.Text = recyclerData;
        }

        /// <summary>
        /// Method: IsRecordValid()
        /// Purpose: Check text fields for empty and invalid values
        /// </summary>
        /// <returns>
        /// True or False
        /// </returns>
        public bool IsRecordValid()
        {
            // set records as valid by default until error found
            bool validRecord = true;
            string errorMessage = "Errors encountered:";

            // check name
            if (String.IsNullOrEmpty(bname_textbox.Text))
            {
                errorMessage += "\nBusiness Name is required";
                validRecord = false;
            }

            // check address
            if (String.IsNullOrEmpty(address_textbox.Text))
            {
                errorMessage += "\nAddress is required";
                validRecord = false;
            }

            // check phone
            if (String.IsNullOrEmpty(phone_textbox.Text))
            {
                errorMessage += "\nPhone Number is required";
                validRecord = false;
            }

            // check website
            if (String.IsNullOrEmpty(website_textbox.Text))
            {
                errorMessage += "\nWebsite is required";
                validRecord = false;
            }
            else if (!IsValidURL(website_textbox.Text))
            {
                errorMessage += "\nWebsite is not a valid URL";
                errorMessage += "\nMust start with http:// or https://";
                errorMessage += "\nMust be a properly formed URL";
                validRecord = false;
            }

            // check recycles
            if (String.IsNullOrEmpty(recycles_textbox.Text))
            {
                errorMessage += "\nRecycled Material is required";
                validRecord = false;
            }

            // display error message if not valid record
            if (!validRecord)
            {
                MessageBox.Show(errorMessage, "Error");
            }

            // return true or false
            return validRecord;
        }

        /// <summary>
        /// Method: IsExistingRecord()
        /// Purpose: checks for existing record by business name
        /// </summary>
        /// <returns>
        /// True or False
        /// </returns>
        public bool IsExistingRecord()
        {
            bool exisitingRecord = false;
            string bname = bname_textbox.Text;
            for (int i = 0; i < recyclerList.Count; i++)
            {
                if (bname.Equals(recyclerList[i].Name))
                {
                    MessageBox.Show("Error: Record Exists", "Error");
                    exisitingRecord = true;
                }
            }
            return exisitingRecord;
        }

        /// <summary>
        /// Method: IsValidURL()
        /// Purpose: checks that URL is a properly formed URL
        /// </summary>
        /// <param name="url"></param>
        /// <returns>
        /// True or False
        /// </returns>
        public bool IsValidURL(string url)
        {
            Uri uriResult;
            bool validURL = Uri.TryCreate(url, UriKind.Absolute, out uriResult)
                && (uriResult.Scheme == Uri.UriSchemeHttp || uriResult.Scheme == Uri.UriSchemeHttps);
            return validURL;
        }

        /// <summary>
        /// Method: BinarySearchName()
        /// Purpose: perform a binary search on entered name
        /// </summary>
        /// <returns>
        /// integer of position found, or -1 for not found
        /// </returns>
        public int BinarySearchName()
        {
            // set found status as false
            bool foundStatus = false;
            // set first value to 0
            int first = 0;
            // set last value as number of recyclers
            int last = recyclerList.Count - 1;
            // declare mid variable
            int mid;
            // set found position as -1 (not found)
            int posFound = -1;

            while (!foundStatus && first <= last)
            {
                mid = (first + last) / 2;

                // determine if name is potentially in lower half of list 
                if (search_textbox.Text.CompareTo(recyclerList[mid].Name) < 0)
                {
                    last = mid - 1;
                }
                // determine if name is potentially in upper half of list
                else if (search_textbox.Text.CompareTo(recyclerList[mid].Name) > 0)
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
            // return position found, or -1 for not found
            return posFound;
        }

        /// <summary>
        /// Action performed on first button click
        /// set current record to first
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void first_button_Click(object sender, EventArgs e)
        {
            currentRecord = 0;
            DisplayCurrentRecord();
        }

        /// <summary>
        /// Action performed on previous button click
        /// set current record to previous record
        /// if at first record already, go to last record (cycle through)
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void prev_button_Click(object sender, EventArgs e)
        {
            currentRecord--;
            if (currentRecord < 0)
            {
                currentRecord = recyclerList.Count - 1;
            }
            DisplayCurrentRecord();
        }

        /// <summary>
        /// Action performed on next button click
        /// set current record to next record
        /// if at last record already, go to first record (cycle through)
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void next_button_Click(object sender, EventArgs e)
        {
            currentRecord++;
            if (currentRecord >= recyclerList.Count)
            {
                currentRecord = 0;
            }
            DisplayCurrentRecord();
        }

        /// <summary>
        /// Action performed on last button click
        /// set current record to last
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void last_button_Click(object sender, EventArgs e)
        {
            currentRecord = recyclerList.Count - 1;
            DisplayCurrentRecord();
        }

        /// <summary>
        /// Action performed when Go to URL button click
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void url_button_Click(object sender, EventArgs e)
        {
            // check text box not empty
            if (website_textbox.Text != string.Empty)
            {
                try
                {
                    // check url is valid
                    if (IsValidURL(website_textbox.Text))
                    {
                        // launch process using default http or https shell execute
                        Process.Start(new ProcessStartInfo
                        {
                            FileName = website_textbox.Text,
                            UseShellExecute = true
                        });
                    }
                    else
                    {
                        // show message if not valid url
                        MessageBox.Show("Website is not a valid URL", "Error");
                    }
                }
                catch (Exception ex)
                {
                    MessageBox.Show(ex.Message);
                }

            }
        }

        /// <summary>
        /// Action performed when entering the search text box
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void search_textbox_Enter(object sender, EventArgs e)
        {
            // check if text is default
            if (search_textbox.Text == "Enter Business Name to Search")
            {
                // empty text box
                search_textbox.Text = string.Empty;
            }
        }

        /// <summary>
        /// Action performed when leaving text box
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void search_textbox_Leave(object sender, EventArgs e)
        {
            // check is t4ext box is empty
            if (search_textbox.Text == string.Empty)
            {
                // replace empty text box with default text
                search_textbox.Text = "Enter Business Name to Search";
            }
        }

        /// <summary>
        /// Action performed when search button clicked
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void search_button_Click(object sender, EventArgs e)
        {
            // set found position to result of binary search method
            int posFound = BinarySearchName();
            // check if found
            if (posFound == -1)
            {
                // display message if not found
                MessageBox.Show("Not found");
            }
            else
            {
                // if found, show position found and set as currently shown record
                MessageBox.Show("Found at position: " + (posFound + 1));
                currentRecord = posFound;
                DisplayCurrentRecord();
            }
        }

        /// <summary>
        /// Action performed on clear button click
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void clear_button_Click(object sender, EventArgs e)
        {
            // set all text boxes to empty
            bname_textbox.Text = string.Empty;
            address_textbox.Text = string.Empty;
            phone_textbox.Text = string.Empty;
            website_textbox.Text = string.Empty;
            recycles_textbox.Text = string.Empty;
        }

        /// <summary>
        /// action performed on add new record button click
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void addnew_button_Click(object sender, EventArgs e)
        {
            // check if data is valid
            if (!IsRecordValid())
            {
                return;
            }
            // check if already existing
            else if (IsExistingRecord())
            {
                return;
            }
            else
            {
                // confirm add new record
                DialogResult dialogResult = MessageBox.Show("Add new record?", "Add record", MessageBoxButtons.YesNo);
                if (dialogResult == DialogResult.Yes)
                {
                    // extract data from text boxes
                    string bname = bname_textbox.Text;
                    string address = address_textbox.Text;
                    string phone = phone_textbox.Text;
                    string website = website_textbox.Text;
                    string recycles = "";
                    // build recycles data using delimiter " - "
                    string[] recyclesArray = recycles_textbox.Text.Split(new string[] { "\r\n" }, StringSplitOptions.None);
                    recycles += recyclesArray[0];
                    for (int i = 1; i < recyclesArray.Length; i++)
                    {
                        if (recyclesArray[i] != string.Empty)
                        {
                            recycles += " - " + recyclesArray[i];
                        }
                    }
                    // add new record to recycler list, sort and display
                    Recycler newRecycler = new Recycler(bname, address, phone, website, recycles);
                    recyclerList.Add(newRecycler);
                    recyclerList.Sort();
                    DisplayRecyclerData();
                    currentRecord = recyclerList.IndexOf(newRecycler);
                    DisplayCurrentRecord();
                    // rebuild filter by combobox in case of new materials entered
                    CreateMaterialList();
                }
            }
        }

        /// <summary>
        /// Action performed on update record button click
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void update_button_Click(object sender, EventArgs e)
        {
            // check there is data to update
            if (recyclerList.Count < 1)
            {
                MessageBox.Show("No records to update", "Error");
                return;
            }
            // check data is valid
            if (!IsRecordValid())
            {
                return;
            }
            else
            {
                // confirm update record
                string originalBName = recyclerList[currentRecord].Name;
                DialogResult dialogResult = MessageBox.Show("Update record: " + originalBName + "?",
                    "Update record", MessageBoxButtons.YesNo);
                if (dialogResult == DialogResult.Yes)
                {
                    // extract data from text fields
                    string bname = bname_textbox.Text;
                    string address = address_textbox.Text;
                    string phone = phone_textbox.Text;
                    string website = website_textbox.Text;
                    string recycles = "";
                    // build recyles data using delimiter " - "
                    string[] recyclesArray = recycles_textbox.Text.Split(new string[] { "\r\n" }, StringSplitOptions.None);
                    recycles += recyclesArray[0];
                    for (int i = 1; i < recyclesArray.Length; i++)
                    {
                        if (recyclesArray[i] != string.Empty)
                        {
                            recycles += " - " + recyclesArray[i];
                        }
                    }
                    // update record with new data, sort and display
                    Recycler updateRecycler = new Recycler(bname, address, phone, website, recycles);
                    recyclerList[currentRecord] = updateRecycler;
                    recyclerList.Sort();
                    DisplayRecyclerData();
                    currentRecord = recyclerList.IndexOf(updateRecycler);
                    DisplayCurrentRecord();
                    // rebuild filter by combobox in case of new materials entered
                    CreateMaterialList();
                }
            }
        }

        /// <summary>
        /// Action performed on delete button click
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void delete_button_Click(object sender, EventArgs e)
        {
            // check for records
            if (recyclerList.Count > 0)
            {
                // confirm delete record
                string bName = recyclerList[currentRecord].Name;
                DialogResult dialogResult = MessageBox.Show("Delete record: " + bName + "?", "Delete Record", MessageBoxButtons.YesNo);
                if (dialogResult == DialogResult.Yes)
                {
                    // remove record, sort and display
                    recyclerList.Remove(recyclerList[currentRecord]);
                    recyclerList.Sort();
                    DisplayRecyclerData();
                    // if deleted record was last in list, set current record as new last record
                    if (currentRecord >= recyclerList.Count)
                    {
                        currentRecord = recyclerList.Count - 1;
                    }
                    DisplayCurrentRecord();
                    // rebuild filter by combobox in case of removed materials
                    CreateMaterialList();
                }
            }
        }

        /// <summary>
        /// Action performed when save and exit button clicked
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void exit_button_Click(object sender, EventArgs e)
        {
            // confirm save and exit
            DialogResult dialogResult = MessageBox.Show("Save changes and exit?",
                "Save and exit", MessageBoxButtons.YesNo);
            if (dialogResult == DialogResult.Yes)
            {
                try
                {
                    string filePath = @"recyclers.csv";
                    StreamWriter writer = new StreamWriter(filePath);
                    // check if data to write
                    if (recyclerList.Count > 0)
                    {
                        // write records to csv
                        for (int i = 0; i < recyclerList.Count; i++)
                        {
                            writer.WriteLine(recyclerList[i].ToCSVString());
                        }
                    }
                    else
                    {
                        // if no data, write blank file
                        writer.WriteLine("");
                    }
                    writer.Close();
                }
                catch (Exception ex)
                {
                    MessageBox.Show(ex.Message);
                }

                // close application 
                if (System.Windows.Forms.Application.MessageLoop)
                {
                    // WinForms app
                    System.Windows.Forms.Application.Exit();
                }
                else
                {
                    // console app
                    System.Environment.Exit(0);
                }
            }
        }

        /// <summary>
        /// Action performed when filter by comboxbox item changed
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void filterby_combobox_SelectedIndexChanged(object sender, EventArgs e)
        {
            DisplayRecyclerData();
        }
    }
}
