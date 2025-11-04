@file:Suppress("DEPRECATION")

package edu.ifba.educa_ra.telas

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.internal.ContextUtils.getActivity
import com.google.ar.core.ArCoreApk
import edu.ifba.educa_ra.ErroActivity
import edu.ifba.educa_ra.R
import edu.ifba.educa_ra.api.GetConteudos
import edu.ifba.educa_ra.api.GetObjeto
import edu.ifba.educa_ra.arcore.VisualizadorARCoreActivity
import edu.ifba.educa_ra.confirmar
import edu.ifba.educa_ra.dados.AppDatabase
import edu.ifba.educa_ra.dados.dao.ConteudoDao
import edu.ifba.educa_ra.databinding.FragmentConteudosBinding
import edu.ifba.educa_ra.dados.modelo.ConteudoModelo
import edu.ifba.educa_ra.dados.modelo.ObjetoSelecionado
import edu.ifba.educa_ra.decidir
import edu.ifba.educa_ra.visualizador3D.Visualizador3DActivity
import java.io.File

class ConteudosHolder(view: View) : RecyclerView.ViewHolder(view) {
    val card: CardView
    val nomeConteudo: TextView
    val detalhesConteudo: TextView
    val downloadObjeto: ImageButton
    val removerObjeto: ImageButton
    val verObjetos: ImageButton

    init {
        card = view.findViewById(R.id.card_conteudo) as CardView
        nomeConteudo = view.findViewById(R.id.nome_conteudo)
        detalhesConteudo = view.findViewById(R.id.detalhes_conteudo)
        downloadObjeto = view.findViewById(R.id.download_objeto)
        removerObjeto = view.findViewById(R.id.remover_objeto)
        verObjetos = view.findViewById(R.id.ver_objetos)
    }
}


class ConteudosAdapter() :
    RecyclerView.Adapter<ConteudosHolder>() {

    private lateinit var holder: ConteudosHolder

    private lateinit var contexto: Context
    private lateinit var progresso: ProgressBar
    private lateinit var conteudos: List<ConteudoModelo>
    private lateinit var fragment: ConteudosFragment

    private lateinit var cartaoSelecionado: CardView

    constructor(contexto: Context, progresso: ProgressBar, conteudos: List<ConteudoModelo>, fragment: ConteudosFragment) : this() {
        this.contexto = contexto
        this.progresso = progresso
        this.conteudos = conteudos
        this.fragment = fragment
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConteudosHolder {
        val cardConteudo =
            LayoutInflater.from(parent.context).inflate(R.layout.card_conteudo, parent, false)

        return ConteudosHolder(cardConteudo).also { holder = it }
    }

    @SuppressLint("NotifyDataSetChanged")
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onBindViewHolder(holder: ConteudosHolder, position: Int) {
        val conteudo = conteudos[position]

        indicarConteudoDisponivel(holder.card, caminhoConteudo(conteudo))

        holder.nomeConteudo.text = conteudo.nome
        holder.detalhesConteudo.text = conteudo.detalhes
        holder.downloadObjeto.setOnClickListener {
            confirmar(this.contexto,
                "Caso já tenha realizado o download, esta opção irá apagar os arquivos anteriores. Confirma?",
                {
                    progresso.visibility = ProgressBar.VISIBLE
                    GetObjeto(conteudo, contexto.filesDir, ::onProgresso, ::onDownloadFinalizado).execute()
                }, {})
        }
        holder.removerObjeto.setOnClickListener {
            confirmar(this.contexto,
                "Esta opção irá apagar os arquivos deste conteúdo definitivamente. Confirma?",
                {
                    val destino = "${contexto.filesDir.absolutePath}/objeto.${conteudo.id}"

                    val dir = File(destino)
                    if (dir.exists()) {
                        dir.deleteRecursively()
                    }

                    notifyDataSetChanged()
                }, {})}

        (View.OnClickListener{
            DataHolder.objetoSelecionado.nome = conteudo.nome
            DataHolder.objetoSelecionado.detalhes = conteudo.detalhes

            if (conteudoDisponivel(conteudo)) {
                onObjeto(caminhoConteudo(conteudo))
            } else {
                progresso.visibility = ProgressBar.VISIBLE
                GetObjeto(conteudo, contexto.filesDir, ::onProgresso, ::onObjeto).execute()
            }
        }).also { holder.card.setOnClickListener(it)}.also {
            holder.verObjetos.setOnClickListener(it)
        }
    }

    private fun onProgresso(passo: Int) {
        progresso.progress = passo * 20
    }

    private fun caminhoConteudo(conteudo: ConteudoModelo): String {
        return "${contexto.filesDir.absolutePath}/objeto.${conteudo.id}"
    }

    private fun conteudoDisponivel(caminho: String): Boolean {
        val arquivos = File(caminho)

        return arquivos.isDirectory && arquivos.exists()
    }

    private fun conteudoDisponivel(conteudo: ConteudoModelo): Boolean {
        return conteudoDisponivel(caminhoConteudo(conteudo))
    }

    private fun indicarConteudoDisponivel(card: CardView, caminho: String) {
        card.setCardBackgroundColor(
            if (conteudoDisponivel(caminho))
                ContextCompat.getColor(contexto, R.color.card_conteudo_disponivel)
            else
                ContextCompat.getColor(contexto, R.color.card_disciplina)
        )
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun onDownloadFinalizado(caminho: String) {
        progresso.visibility = ProgressBar.INVISIBLE

        notifyDataSetChanged()
    }

    @RequiresApi(Build.VERSION_CODES.R)
    @SuppressLint("RestrictedApi", "NotifyDataSetChanged")
    private fun onObjeto(caminho: String) {
        DataHolder.objetoSelecionado.caminho = caminho
        onDownloadFinalizado(caminho)

        val activity = getActivity(this.contexto)

        val disponibilidade = ArCoreApk.getInstance().checkAvailability(this.contexto)
        if (disponibilidade.isSupported) {
            if (disponibilidade == ArCoreApk.Availability.SUPPORTED_INSTALLED) {
                val visualizador = Intent(activity, VisualizadorARCoreActivity::class.java)
                activity?.startActivity(visualizador)
            } else  {
                decidir(contexto, "você precisa instalar ou atualizar o ARCore ou pode visualizar\n" +
                        "diretamente caso o seu dispositivo não seja compatível.\n" +
                        "O que deseja fazer?",
                    "visualizar", { this.onVisualizarObjeto(activity!!) }, "instalar",
                    { this.onInstalarARCore(activity!!) })
            }
        } else {
            decidir(contexto, "seu dispositivo não tem suporte ao ARCore, mas você pode visualizar\n" +
                    "o conteúdo sem Realidade Aumentada.\n" +
                    "O que deseja fazer?",
                "visualizar", { this.onVisualizarObjeto(activity!!) }, "voltar",
                { notifyDataSetChanged() })
        }
    }

    private fun onInstalarARCore(activity: Activity) {
        ArCoreApk.getInstance().requestInstall(activity, true)
    }

    private fun onVisualizarObjeto(activity: Activity) {
        val visualizador = Intent(activity, Visualizador3DActivity::class.java)

        visualizador.putExtra("object", DataHolder.objetoSelecionado.nome)
        visualizador.putExtra("model", "${DataHolder.objetoSelecionado.caminho}/${DataHolder.objetoSelecionado.nome}.glb")

        activity.startActivity(visualizador)
    }

    override fun getItemCount(): Int {
        return conteudos.count()
    }

}

class DataHolder {
    companion object {
        var objetoSelecionado: ObjetoSelecionado = ObjetoSelecionado("", "", "")
    }
}

class ConteudosFragment : Fragment() {
    private var _binding: FragmentConteudosBinding? = null
    private val binding get() = _binding!!

    private lateinit var database: AppDatabase
    private lateinit var conteudoDao: ConteudoDao

    @SuppressLint("RestrictedApi")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentConteudosBinding.inflate(inflater, container, false)

        database = AppDatabase.instancia(requireContext())
        conteudoDao = database.conteudoDao()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val layout = LinearLayoutManager(this.context)
        binding.conteudos.layoutManager = layout

        val idAula = this.arguments?.getString("idAula")
        idAula?.let { GetConteudos(it, ::onConteudos).execute() }
    }

    private fun getConteudosLocal(): List<ConteudoModelo>? {
        val idAula = this.arguments?.getString("idAula")

        return idAula?.let { conteudoDao.buscaPorAula(it) }
    }

    private fun onConteudos(conteudos: List<ConteudoModelo>) {
        if (conteudos.isEmpty()) {
            binding.conteudos.adapter = getConteudosLocal()?.let {
                ConteudosAdapter(this.requireContext(), binding.progresso,
                    it, this)
            }
        } else {
            binding.conteudos.adapter = ConteudosAdapter(this.requireContext(), binding.progresso,
                conteudos, this)
        }

        val adapter = ConteudosAdapter(this.requireContext(), binding.progresso, conteudos, this)
        binding.conteudos.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}