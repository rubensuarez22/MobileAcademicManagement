import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.project.ClassItemTeacher
import com.example.project.R


class TeacherClassAdapter(private val classList: List<ClassItemTeacher>) : RecyclerView.Adapter<TeacherClassViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeacherClassViewHolder {
        // Asegúrate de que el nombre del layout coincida con el archivo XML que definiste.
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_class_teacher, parent, false)
        return TeacherClassViewHolder(view)
    }

    override fun onBindViewHolder(holder: TeacherClassViewHolder, position: Int) {
        val classItem = classList[position]
        holder.bind(classItem)
    }

    override fun getItemCount(): Int = classList.size
}
