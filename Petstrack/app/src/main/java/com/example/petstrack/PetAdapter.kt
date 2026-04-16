package com.example.petstrack

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load

class PetAdapter(
    private var petList: List<Pet>,
    private val onDeleteClick: (Pet) -> Unit = {},
    private val onPetClick: (Pet) -> Unit = {}
) : RecyclerView.Adapter<PetAdapter.PetViewHolder>() {

    class PetViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val petImageView: ImageView = itemView.findViewById(R.id.itemPetImageView)
        val nameTextView: TextView = itemView.findViewById(R.id.itemNameTextView)
        val speciesTextView: TextView = itemView.findViewById(R.id.itemSpeciesTextView)
        val genderTextView: TextView = itemView.findViewById(R.id.itemGenderTextView)
        val healthSummaryTextView: TextView = itemView.findViewById(R.id.itemHealthSummaryTextView)
        val btnDeletePet: ImageView = itemView.findViewById(R.id.btnDeletePet)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PetViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pet, parent, false)
        return PetViewHolder(view)
    }

    override fun onBindViewHolder(holder: PetViewHolder, position: Int) {
        val pet = petList[position]
        holder.nameTextView.text = pet.name
        holder.speciesTextView.text = "${pet.species} (${pet.age} yrs)"
        holder.genderTextView.text = "Gender: ${pet.gender}"
        
        val healthStatus = if (pet.isSterilized) "Sterilized" else "Not Sterilized"
        holder.healthSummaryTextView.text = "Health: $healthStatus\nVaccinated: ${pet.rabiesVaccinatedDate}\nFavorite Food: ${pet.favoriteFood}\nIssues: ${pet.healthIssues}"

        if (pet.imageUrl.isNotEmpty()) {
            holder.petImageView.load(pet.imageUrl) {
                crossfade(true)
                placeholder(android.R.drawable.ic_menu_gallery)
                error(android.R.drawable.ic_menu_report_image)
            }
        } else {
            holder.petImageView.setImageResource(android.R.drawable.ic_menu_gallery)
        }

        holder.itemView.setOnClickListener {
            onPetClick(pet)
        }

        holder.btnDeletePet.setOnClickListener {
            onDeleteClick(pet)
        }
    }

    override fun getItemCount(): Int = petList.size

    fun updatePets(newPets: List<Pet>) {
        petList = newPets
        notifyDataSetChanged()
    }
}