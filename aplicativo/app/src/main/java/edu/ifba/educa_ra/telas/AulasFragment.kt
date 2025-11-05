@file:Suppress("DEPRECATION")

package edu.ifba.educa_ra.telas

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.ifba.educa_ra.R
import edu.ifba.educa_ra.api.GetAulas
import edu.ifba.educa_ra.avisar
import edu.ifba.educa_ra.dados.AppDatabase
import edu.ifba.educa_ra.dados.dao.AulaDao
import edu.ifba.educa_ra.databinding.FragmentAulasBinding
import edu.ifba.educa_ra.dados.modelo.AulaModelo
import edu.ifba.educa_ra.dados.modelo.DisciplinaModelo

class AulasHolder(view: View) : RecyclerView.ViewHolder(view) {
    val card: CardView
    val nomeAula: TextView
    val detalhesAula: TextView
    val verConteudos: ImageButton

    init {
        card = view.findViewById(R.id.card_aula) as CardView
        nomeAula = view.findViewById(R.id.nome_aula)
        detalhesAula = view.findViewById(R.id.detalhes_aula)
        verConteudos = view.findViewById(R.id.ver_conteudos)
    }
}

class AulasAdapter() :
    RecyclerView.Adapter<AulasHolder>() {

    private lateinit var aulas: List<AulaModelo>
    private lateinit var navegador: NavController

    constructor(navegador: NavController, aulas: List<AulaModelo>) : this() {
        this.navegador = navegador
        this.aulas = aulas
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AulasHolder {
        val cardConteudo =
            LayoutInflater.from(parent.context).inflate(R.layout.card_aula, parent, false)

        return AulasHolder(cardConteudo)
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onBindViewHolder(holder: AulasHolder, position: Int) {
        val aula = aulas[position]

        holder.nomeAula.text = aula.nome
        holder.detalhesAula.text = aula.detalhes

        (View.OnClickListener{
            navegador.navigate(
                R.id.mostrar_conteudos,
                bundleOf("idAula" to aula.id)
            )}).also { holder.card.setOnClickListener(it) }.also { holder.verConteudos.setOnClickListener(it) }
    }

    override fun getItemCount(): Int {
        return aulas.count()
    }

}

class AulasFragment : Fragment() {

    private var _binding: FragmentAulasBinding? = null
    private val binding get() = _binding!!

    private lateinit var database: AppDatabase
    private lateinit var aulaDao: AulaDao

    @SuppressLint("RestrictedApi")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAulasBinding.inflate(inflater, container, false)

        database = AppDatabase.instancia(requireContext())
        aulaDao = database.aulaDao()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val layout = LinearLayoutManager(this.context)
        binding.aulas.layoutManager = layout

        val idDisciplina = this.arguments?.getString("idDisciplina")
        idDisciplina?.let { GetAulas(it, ::onAulas).execute() }
    }

    private fun getAulasLocal(): List<AulaModelo>? {
        val idDisciplina = this.arguments?.getString("idDisciplina")

        return idDisciplina?.let { aulaDao.buscaPorDisciplina(it) }
    }

    private fun salvarAulas(aulas: List<AulaModelo>) {
        aulas.forEach {
            aulaDao.salva(it)
        }
    }

    private fun onAulas(aulas: List<AulaModelo>) {
        if (aulas.isEmpty()) {
            val local = getAulasLocal()
            if (!local.isNullOrEmpty()) {
                binding.aulas.adapter = AulasAdapter(findNavController(), local)
            } else {
                this.context?.let { avisar(it, "não existem aulas para exibir") }
            }
        } else {
            salvarAulas(aulas)

            binding.aulas.adapter = AulasAdapter(findNavController(), aulas)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}