import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.atividade1.R
import com.example.atividade1.model.Aluno

class AlunoAdapter(
    private val listaAlunos: List<Aluno>,
    private val onItemClick: (Aluno) -> Unit
) : RecyclerView.Adapter<AlunoAdapter.AlunoViewHolder>() {

    inner class AlunoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nomeAluno: TextView = itemView.findViewById(R.id.txtNomeAluno)
        val imgAluno: ImageView = itemView.findViewById(R.id.imgAluno)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlunoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_aluno, parent, false)
        return AlunoViewHolder(view)
    }

    override fun onBindViewHolder(holder: AlunoViewHolder, position: Int) {
        val aluno = listaAlunos[position]
        holder.nomeAluno.text = aluno.nome

        if (!aluno.imageBase64.isNullOrEmpty()) {
            try {
                val decodedBytes = Base64.decode(aluno.imageBase64, Base64.DEFAULT)
                val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
                holder.imgAluno.setImageBitmap(bitmap)
            } catch (e: Exception) {
                holder.imgAluno.setImageResource(R.drawable.unifit)
            }
        } else {
            holder.imgAluno.setImageResource(R.drawable.unifit)
        }

        holder.itemView.setOnClickListener {
            onItemClick(aluno)
        }
    }

    override fun getItemCount(): Int = listaAlunos.size
}
