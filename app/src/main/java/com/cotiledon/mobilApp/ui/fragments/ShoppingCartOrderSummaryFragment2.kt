package com.cotiledon.mobilApp.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.cotiledon.mobilApp.R
import com.cotiledon.mobilApp.ui.backend.user.RetrofitUserClient
import com.cotiledon.mobilApp.ui.dataClasses.profile.UserAddressCreation
import com.cotiledon.mobilApp.ui.managers.OrderManager
import com.cotiledon.mobilApp.ui.managers.TokenManager
import kotlinx.coroutines.launch

class ShoppingCartOrderSummaryFragment2 : Fragment() {
    private lateinit var regionSpinner: Spinner
    private lateinit var comunaSpinner: Spinner
    // Add new fields for street address components
    private lateinit var streetNameEditText: EditText
    private lateinit var streetNumberEditText: EditText
    private lateinit var departmentEditText: EditText  // For apartment/unit number
    private lateinit var referenceEditText: EditText   // For delivery instructions/reference
    private lateinit var receiverNameEditText: EditText
    private lateinit var finalizeButton: Button
    private lateinit var modifyInfoText: TextView

    private val tokenManager: TokenManager by lazy {
        context?.let { TokenManager(it) } ?: throw IllegalStateException("Contexto no disponible")
    }

    private val regions = arrayOf(
        "Seleccione una Región",
        "Arica y Parinacota",
        "Tarapacá",
        "Antofagasta",
        "Atacama",
        "Coquimbo",
        "Valparaíso",
        "Metropolitana de Santiago",
        "O'Higgins",
        "Maule",
        "Ñuble",
        "Biobío",
        "Araucanía",
        "Los Ríos",
        "Los Lagos",
        "Aysén",
        "Magallanes"
    )

    private val communesByRegion = mapOf(
        "Seleccione una Región" to arrayOf("Seleccione una Comuna"),
        "Arica y Parinacota" to arrayOf("Arica", "Camarones", "Putre", "General Lagos"),
        "Tarapacá" to arrayOf("Iquique", "Alto Hospicio", "Pozo Almonte", "Camiña", "Colchane", "Huara", "Pica"),
        "Antofagasta" to arrayOf("Antofagasta", "Mejillones", "Sierra Gorda", "Taltal", "Calama", "Ollagüe", "San Pedro de Atacama", "Tocopilla", "María Elena"),
        "Atacama" to arrayOf("Copiapó", "Caldera", "Tierra Amarilla", "Chañaral", "Diego de Almagro", "Vallenar", "Alto del Carmen", "Freirina", "Huasco"),
        "Coquimbo" to arrayOf("La Serena", "Coquimbo", "Andacollo", "La Higuera", "Paiguano", "Vicuña", "Illapel", "Canela", "Los Vilos", "Salamanca", "Ovalle", "Combarbalá", "Monte Patria", "Punitaqui", "Río Hurtado"),
        "Valparaíso" to arrayOf("Valparaíso", "Casablanca", "Concón", "Juan Fernández", "Puchuncaví", "Quintero", "Viña del Mar", "Isla de Pascua", "Los Andes", "Calle Larga", "Rinconada", "San Esteban", "La Ligua", "Cabildo", "Papudo", "Petorca", "Zapallar", "Quillota", "Calera", "Hijuelas", "La Cruz", "Nogales", "San Antonio", "Algarrobo", "Cartagena", "El Quisco", "El Tabo", "Santo Domingo", "San Felipe", "Catemu", "Llaillay", "Panquehue", "Putaendo", "Santa María", "Quilpué", "Limache", "Olmué", "Villa Alemana"),
        "Metropolitana de Santiago" to arrayOf("Santiago", "Cerrillos", "Cerro Navia", "Conchalí", "El Bosque", "Estación Central", "Huechuraba", "Independencia", "La Cisterna", "La Florida", "La Granja", "La Pintana", "La Reina", "Las Condes", "Lo Barnechea", "Lo Espejo", "Lo Prado", "Macul", "Maipú", "Ñuñoa", "Pedro Aguirre Cerda", "Peñalolén", "Providencia", "Pudahuel", "Quilicura", "Quinta Normal", "Recoleta", "Renca", "San Joaquín", "San Miguel", "San Ramón", "Vitacura", "Puente Alto", "Pirque", "San José de Maipo", "Colina", "Lampa", "Tiltil", "San Bernardo", "Buin", "Calera de Tango", "Paine", "Melipilla", "Alhué", "Curacaví", "María Pinto", "San Pedro", "Talagante", "El Monte", "Isla de Maipo", "Padre Hurtado", "Peñaflor"),
        "O'Higgins" to arrayOf("Rancagua", "Codegua", "Coinco", "Coltauco", "Doñihue", "Graneros", "Las Cabras", "Machalí", "Malloa", "Mostazal", "Olivar", "Peumo", "Pichidegua", "Quinta de Tilcoco", "Rengo", "Requínoa", "San Vicente", "Pichilemu", "La Estrella", "Litueche", "Marchigüe", "Navidad", "Paredones", "San Fernando", "Chépica", "Chimbarongo", "Lolol", "Nancagua", "Palmilla", "Peralillo", "Placilla", "Pumanque", "Santa Cruz"),
        "Maule" to arrayOf("Talca", "Constitución", "Curepto", "Empedrado", "Maule", "Pelarco", "Pencahue", "Río Claro", "San Clemente", "San Rafael", "Cauquenes", "Chanco", "Pelluhue", "Curicó", "Hualañé", "Licantén", "Molina", "Rauco", "Romeral", "Sagrada Familia", "Teno", "Vichuquén", "Linares", "Colbún", "Longaví", "Parral", "Retiro", "San Javier", "Villa Alegre", "Yerbas Buenas"),
        "Ñuble" to arrayOf("Chillán", "Bulnes", "Cobquecura", "Coelemu", "Coihueco", "El Carmen", "Ninhue", "Ñiquén", "Pemuco", "Pinto", "Portezuelo", "Quillón", "Quirihue", "Ránquil", "San Carlos", "San Fabián", "San Ignacio", "San Nicolás", "Treguaco", "Yungay"),
        "Biobío" to arrayOf("Concepción", "Coronel", "Chiguayante", "Florida", "Hualqui", "Lota", "Penco", "San Pedro de la Paz", "Santa Juana", "Talcahuano", "Tomé", "Hualpén", "Lebu", "Arauco", "Cañete", "Contulmo", "Curanilahue", "Los Álamos", "Tirúa", "Los Ángeles", "Antuco", "Cabrero", "Laja", "Mulchén", "Nacimiento", "Negrete", "Quilaco", "Quilleco", "San Rosendo", "Santa Bárbara", "Tucapel", "Yumbel", "Alto Biobío"),
        "Araucanía" to arrayOf("Temuco", "Carahue", "Cunco", "Curarrehue", "Freire", "Galvarino", "Gorbea", "Lautaro", "Loncoche", "Melipeuco", "Nueva Imperial", "Padre Las Casas", "Perquenco", "Pitrufquén", "Pucón", "Saavedra", "Teodoro Schmidt", "Toltén", "Vilcún", "Villarrica", "Cholchol", "Angol", "Collipulli", "Curacautín", "Ercilla", "Lonquimay", "Los Sauces", "Lumaco", "Purén", "Renaico", "Traiguén", "Victoria"),
        "Los Ríos" to arrayOf("Valdivia", "Corral", "Lanco", "Los Lagos", "Máfil", "Mariquina", "Paillaco", "Panguipulli", "La Unión", "Futrono", "Lago Ranco", "Río Bueno"),
        "Los Lagos" to arrayOf("Puerto Montt", "Calbuco", "Cochamó", "Fresia", "Frutillar", "Los Muermos", "Llanquihue", "Maullín", "Puerto Varas", "Castro", "Ancud", "Chonchi", "Curaco de Vélez", "Dalcahue", "Puqueldón", "Queilén", "Quellón", "Quemchi", "Quinchao", "Osorno", "Puerto Octay", "Purranque", "Puyehue", "Río Negro", "San Juan de la Costa", "San Pablo", "Chaitén", "Futaleufú", "Hualaihué", "Palena"),
        "Aysén" to arrayOf("Coyhaique", "Lago Verde", "Aysén", "Cisnes", "Guaitecas", "Cochrane", "O'Higgins", "Tortel", "Chile Chico", "Río Ibáñez"),
        "Magallanes" to arrayOf("Punta Arenas", "Laguna Blanca", "Río Verde", "San Gregorio", "Cabo de Hornos", "Antártica", "Porvenir", "Primavera", "Timaukel", "Natales", "Torres del Paine")
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_shopping_cart_order_summary_layout_2, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeViews(view)
        setupSpinners()
        setupClickListeners()
        loadExistingDetails()
    }

    private fun initializeViews(view: View) {
        regionSpinner = view.findViewById(R.id.spinner_region)
        comunaSpinner = view.findViewById(R.id.spinner_comuna)
        streetNameEditText = view.findViewById(R.id.street_name_edit_text)
        streetNumberEditText = view.findViewById(R.id.street_number_edit_text)
        departmentEditText = view.findViewById(R.id.department_edit_text)
        referenceEditText = view.findViewById(R.id.reference_edit_text)
        receiverNameEditText = view.findViewById(R.id.receiver_name_edit_text)
        finalizeButton = view.findViewById(R.id.btn_siguiente)
        modifyInfoText = view.findViewById(R.id.TextViewInformation)
    }

    private fun setupSpinners() {
        // Create adapter for regions
        val regionAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            regions // Now this is a properly typed Array<String>
        )
        regionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        regionSpinner.adapter = regionAdapter

        // Create initial adapter for communes with default option
        val defaultCommuneAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            arrayOf("Seleccione una Comuna") // Use Array instead of List
        )
        defaultCommuneAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        comunaSpinner.adapter = defaultCommuneAdapter

        // Set up the region selection listener
        regionSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedRegion = regions[position]

                // Get communes for selected region
                val communes = communesByRegion[selectedRegion] ?:
                arrayOf("Seleccione una Comuna")

                // Create adapter for communes
                val communeAdapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_item,
                    communes // This is now properly typed as Array<String>
                )
                communeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                comunaSpinner.adapter = communeAdapter
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Reset to default commune adapter
                comunaSpinner.adapter = defaultCommuneAdapter
            }
        }

        // Load existing details if available
        OrderManager.shippingDetails?.let { details ->
            if (details.region.isNotEmpty()) {
                // Find index of saved region
                val regionIndex = regions.indexOf(details.region)
                if (regionIndex != -1) {
                    regionSpinner.setSelection(regionIndex)

                    // Get communes for this region
                    val communes = communesByRegion[details.region]
                    if (communes != null && details.commune.isNotEmpty()) {
                        val communeIndex = communes.indexOf(details.commune)
                        if (communeIndex != -1) {
                            comunaSpinner.setSelection(communeIndex)
                        }
                    }
                }
            }
        }
    }

    private fun setupClickListeners() {
        finalizeButton.setOnClickListener {
            if (validateInputs()) {
                /*viewLifecycleOwner.lifecycleScope.launch {
                    try {
                        if(tokenManager.isVisitor()) {
                            val visitorAddress = OrderManager.getVisitorDetails()
                            if(visitorAddress != null) {
                                val addressResponse = RetrofitUserClient.createUserClient(tokenManager).createAddress(
                                    UserAddressCreation(
                                        nombre = visitorAddress.name,
                                        apellido = visitorAddress.lastName,
                                        region = regionSpinner.selectedItem.toString(),
                                        comuna = comunaSpinner.selectedItem.toString(),
                                        calle = streetNameEditText.text.toString(),
                                        numero = streetNumberEditText.text.toString(),
                                        departamento = departmentEditText.text.toString(),
                                        referencia = referenceEditText.text.toString()
                                    )
                                )
                                if(addressResponse.isSuccessful) {
                                    val message = addressResponse.body()
                                    if(message != null) {
                                        Log.i("ShoppingCartFragmentPay", "$message")
                                    }
                                }
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("ShoppingCartFragmentPay", "Error updating visitor profile", e)
                    }*/

                saveAddressDetails()
                navigateToPayment()
            }
        }
        modifyInfoText.setOnClickListener {
            enableEditing()
        }
    }



    private fun validateInputs(): Boolean {
        var isValid = true

        if (regionSpinner.selectedItem.toString() == "Seleccione una Región") {
            (regionSpinner.selectedView as? TextView)?.error = "Seleccione una región"
            isValid = false
        }

        if (comunaSpinner.selectedItem.toString() == "Seleccione una Comuna") {
            (comunaSpinner.selectedView as? TextView)?.error = "Seleccione una comuna"
            isValid = false
        }

        if (streetNameEditText.text.toString().isBlank()) {
            streetNameEditText.error = "Ingrese el nombre de la calle"
            isValid = false
        }

        if (streetNumberEditText.text.toString().isBlank()) {
            streetNumberEditText.error = "Ingrese el número"
            isValid = false
        }

        if (receiverNameEditText.text.toString().isBlank()) {
            receiverNameEditText.error = "Ingrese el nombre de quien recibe"
            isValid = false
        }

        return isValid
    }

    private fun saveAddressDetails() {
        val currentDetails = OrderManager.shippingDetails
        OrderManager.shippingDetails = currentDetails?.copy(
            address = streetNameEditText.text.toString(),
            commune = comunaSpinner.selectedItem.toString(),
            region = regionSpinner.selectedItem.toString(),
            streetNumber = streetNumberEditText.text.toString(),
            department = departmentEditText.text.toString(),
            reference = referenceEditText.text.toString()
        )
    }

    @SuppressLint("SetTextI18n")
    private fun loadExistingDetails() {
        // Cargar los detalles de envío si ya existen
        OrderManager.shippingDetails?.let { details ->
            if (details.address.isNotEmpty()) {
                streetNameEditText.setText(details.address)
            }

            if (details.commune.isNotEmpty()) {
                comunaSpinner.setSelection(communesByRegion[details.region]?.indexOf(details.commune) ?: 0)
            }

            if (details.region.isNotEmpty()) {
                regionSpinner.setSelection(regions.indexOf(details.region))
            }

            if (details.streetNumber != null) {
                streetNumberEditText.setText(details.streetNumber.toString())
            }

            if (details.department != null) {
                departmentEditText.setText(details.department)
            }

            if (details.reference?.isNotEmpty() == true) {
                referenceEditText.setText(details.reference)
            }
            // Settear el nombre de quien recibe. Utilizar el nombre original si no se llena
            receiverNameEditText.setText(details.name + " " + details.lastName)
        }
    }

    private fun enableEditing() {
        streetNameEditText.isEnabled = true
        streetNumberEditText.isEnabled = true
        departmentEditText.isEnabled = true
        referenceEditText.isEnabled = true
        receiverNameEditText.isEnabled = true
        regionSpinner.isEnabled = true
        comunaSpinner.isEnabled = true
    }

    private fun navigateToPayment() {
        if (validateInputs()) {
            saveAddressDetails()

            val paymentFragment = ShoppingCartFragmentPay.newInstance()

            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, paymentFragment)
                .addToBackStack(null)
                .commit()
        }
    }

    companion object {
        fun newInstance() = ShoppingCartOrderSummaryFragment2()
    }
}