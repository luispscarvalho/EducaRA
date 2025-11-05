package edu.ifba.educa_ra.telas

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.ifba.educa_ra.R
import edu.ifba.educa_ra.dados.AppDatabase
import edu.ifba.educa_ra.dados.dao.DisciplinaDao

import edu.ifba.educa_ra.api.GetDisciplinas;
import edu.ifba.educa_ra.avisar
import edu.ifba.educa_ra.dados.modelo.AulaModelo
import edu.ifba.educa_ra.databinding.FragmentDisciplinasBinding
import edu.ifba.educa_ra.dados.modelo.DisciplinaModelo

class DisciplinaHolder(view: View) : RecyclerView.ViewHolder(view) {
    val card: CardView
    val nomeDisciplina: TextView
    val detalhesDisciplina: TextView
    val verAulas: ImageButton

    init {
        card = view.findViewById(R.id.card_disciplina) as CardView
        nomeDisciplina = view.findViewById(R.id.nome_disciplina)
        detalhesDisciplina = view.findViewById(R.id.detalhes_disciplina)
        verAulas = view.findViewById(R.id.ver_aulas)
    }
}

class DisciplinaAdapter() :
    RecyclerView.Adapter<DisciplinaHolder>() {

    private lateinit var navegador: NavController
    private lateinit var disciplinas: List<DisciplinaModelo>

    constructor(navegador: NavController, disciplinas: List<DisciplinaModelo>) : this() {
        this.navegador = navegador
        this.disciplinas = disciplinas
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DisciplinaHolder {
        val cardDisciplina =
            LayoutInflater.from(parent.context).inflate(R.layout.card_disciplina, parent, false)

        return DisciplinaHolder(cardDisciplina)
    }

    override fun onBindViewHolder(holder: DisciplinaHolder, position: Int) {
        val disciplina = disciplinas[position]

        holder.nomeDisciplina.text = disciplina.nome
        holder.detalhesDisciplina.text = disciplina.detalhes

        (View.OnClickListener{
            navegador.navigate(R.id.mostrar_aulas,
                bundleOf("idDisciplina" to disciplina.id)
            )}).also { holder.card.setOnClickListener(it) }.also { holder.verAulas.setOnClickListener(it) }
    }

    override fun getItemCount(): Int {
        return disciplinas.count()
    }
}

@Suppress("DEPRECATION")
class DisciplinasFragment : Fragment() {

    private var _binding: FragmentDisciplinasBinding? = null
    private val binding get() = _binding!!

    private lateinit var database: AppDatabase
    private lateinit var disciplinaDao: DisciplinaDao

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDisciplinasBinding.inflate(inflater, container, false)

        database = AppDatabase.instancia(requireContext())
        disciplinaDao = database.disciplinaDao()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val layout = LinearLayoutManager(this.context)
        binding.disciplinas.layoutManager = layout

        GetDisciplinas(::onDisciplinas).execute()
    }

    private fun salvarDisciplinas(disciplinas: List<DisciplinaModelo>) {
        disciplinas.forEach {
            disciplinaDao.salva(it)
        }
    }

    private fun onDisciplinas(disciplinas: List<DisciplinaModelo>) {
        if (disciplinas.isEmpty()) {
            val local = disciplinaDao.buscaTodos();
            if (local.isNotEmpty()) {
                binding.disciplinas.adapter =
                    DisciplinaAdapter(findNavController(), disciplinaDao.buscaTodos())
                this.context?.let {
                    avisar(
                        it,
                        "ocorreu um problema de acesso aos dados em nuvem, acessando informações gravadas localmente"
                    )
                }
            } else {
                this.context?.let { avisar(it, "não existem disciplinas para exibir") }
            }
        } else {
            salvarDisciplinas(disciplinas)

            binding.disciplinas.adapter = DisciplinaAdapter(findNavController(), disciplinas)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}