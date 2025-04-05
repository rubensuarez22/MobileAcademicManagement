import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.project.ClassItemTeacher
import com.example.project.R


class TeacherClassViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val tvClassName: TextView = itemView.findViewById(R.id.tvClassNameTeacher)
    private val tvIdClass: TextView = itemView.findViewById(R.id.tvIdClassTeacher)
    private val tvHour: TextView = itemView.findViewById(R.id.tvHourTeacher)

    fun bind(classItem: ClassItemTeacher) {
        tvClassName.text = classItem.className
        // En lugar de agregar "Grade: ..." mostramos directamente el dato,
        // asumiendo que 'grade' contiene el ID o el dato que quieres mostrar.
        tvIdClass.text = classItem.idClass
        tvHour.text = classItem.time
    }
}

