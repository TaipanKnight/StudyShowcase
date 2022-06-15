using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace LocalRecyclers
{
    /// <summary>
    /// Recycler class
    /// </summary>
    internal class Recycler : IComparable<Recycler>
    {
        /// <summary>
        /// Public properties : get and set
        /// </summary>
        public string Name { get; set; }
        public string Address { get; set; }
        public string Phone { get; set; }
        public string Website { get; set; }
        public string Recycles { get; set; }

        /// <summary>
        /// Recycler paramaterised constructor method
        /// </summary>
        /// <param name="name"></param>
        /// <param name="address"></param>
        /// <param name="phone"></param>
        /// <param name="website"></param>
        /// <param name="recycles"></param>
        public Recycler(string name, string address, string phone, string website,string recycles)
        {
            this.Name = name;
            this.Address = address;
            this.Phone = phone;
            this.Website = website;

            // May have multiple values
            // Split individual values when used
            // Split by delimiter " - "
            this.Recycles = recycles;
        }

        /// <summary>
        /// Override ToString() method
        /// </summary>
        /// <returns>
        /// String of recycler instance
        /// Name[tab]Phone[tab]Website
        /// </returns>
        public override string ToString()
        {
            string recyclerString = Name + "\t" + Phone + "\t" + Website;
            return recyclerString;
        }

        /// <summary>
        /// Returns recycler instance as CSV string
        /// </summary>
        /// <returns>
        /// Name,Address,Phone,Website,Recycles
        /// </returns>
        public string ToCSVString()
        {
            return Name + "," + Address + "," + Phone + "," +
                Website + "," + Recycles;
        }

        /// <summary>
        /// CompareTo method implementing IComparable
        /// Used in Sort() methods to sort by name
        /// </summary>
        /// <param name="other"></param>
        /// <returns>
        /// Integer greater than, less than or equal to 0
        /// </returns>
        public int CompareTo(Recycler other)
        {
            return this.Name.CompareTo(other.Name);
        }
    }
}
