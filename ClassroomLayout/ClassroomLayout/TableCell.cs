using System;

namespace ClassroomLayout
{
    internal class TableCell : IComparable<TableCell>
    {
        public int Column { get; set; }
        public int Row { get; set; }
        public string Name { get; set; }

        public TableCell(int column, int row, string name)
        {
            this.Column = column;
            this.Row = row;
            this.Name = name;
        }

        public string ToCSVString()
        {
            return Column.ToString() + "," + Row.ToString() + "," + Name;
        }

        public int CompareTo(TableCell? other)
        {
            if (other != null)
            {
                return this.Name.CompareTo(other.Name);
            }
            else
            {
                return 1;
            }
        }

    }

}
