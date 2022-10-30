package org.setu.showcase.adapters


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import org.setu.showcase.databinding.CardPortfolioBinding
import org.setu.showcase.models.PortfolioModel

interface PortfolioListener {
    fun onPortfolioClick(portfolio: PortfolioModel)
}

class PortfolioAdapter constructor(private var portfolios: List<PortfolioModel>,
                                  private val listener: PortfolioListener) :
    RecyclerView.Adapter<PortfolioAdapter.MainHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        val binding = CardPortfolioBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)

        return MainHolder(binding)
    }

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        val portfolio = portfolios[holder.adapterPosition]
        holder.bind(portfolio, listener)
    }

    override fun getItemCount(): Int = portfolios.size

    class MainHolder(private val binding : CardPortfolioBinding) :
        RecyclerView.ViewHolder(binding.root) {
        // Function to bind different values to the portfolio adapter card
        fun bind(portfolio: PortfolioModel, listener: PortfolioListener) {
            binding.portfolioTitle.text = portfolio.title
            binding.portfolioDescription.text = portfolio.description
            binding.portfolioType.text = portfolio.type
            Picasso.get().load(portfolio.image).resize(200,200).into(binding.imageIcon)
            binding.root.setOnClickListener { listener.onPortfolioClick(portfolio) }
        }
    }
}