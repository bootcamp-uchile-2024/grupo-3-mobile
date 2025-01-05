package com.cotiledon.mobilApp.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.cotiledon.mobilApp.R
import com.cotiledon.mobilApp.ui.activities.MainActivity
import com.cotiledon.mobilApp.ui.backend.authentication.RetrofitAuthClient
import com.cotiledon.mobilApp.ui.backend.user.RetrofitUserClient
import com.cotiledon.mobilApp.ui.dataClasses.profile.UserRegistrationData
import com.cotiledon.mobilApp.ui.managers.TokenManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserRegisterNextFragment : Fragment() {
    private lateinit var regionSpinner: Spinner
    private lateinit var comunaSpinner: Spinner
    private lateinit var addressEditText: EditText
    private lateinit var termsCheckBox: CheckBox
    private lateinit var registerButton: Button

    private lateinit var registrationData: UserRegistrationData
    private lateinit var tokenManager: TokenManager
    private lateinit var userClient: RetrofitUserClient
    private lateinit var authClient: RetrofitAuthClient

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_user_register_next, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        registrationData = arguments?.getParcelable(ARG_REGISTRATION_DATA)
            ?: throw IllegalStateException("Registration data required")

        tokenManager = TokenManager(requireContext())
        userClient = RetrofitUserClient.createUserClient(TokenManager(requireContext()))
        authClient = RetrofitAuthClient.createAuthClient()

        initializeViews(view)
        setupSpinners()
        setupClickListeners()
    }

    private fun initializeViews(view: View) {
        regionSpinner = view.findViewById(R.id.spinner_region)
        comunaSpinner = view.findViewById(R.id.spinner_comuna)
        addressEditText = view.findViewById(R.id.edittext_adress)
        termsCheckBox = view.findViewById(R.id.checkbox_terms)
        registerButton = view.findViewById(R.id.button_register_next)
    }

    private fun setupSpinners() {
        val regions = listOf(
            "Arica y Parinacota", "Tarapacá", "Antofagasta", "Atacama", "Coquimbo",
            "Valparaíso", "Metropolitana de Santiago", "O'Higgins", "Maule",
            "Ñuble", "Biobío", "Araucanía", "Los Ríos", "Los Lagos",
            "Aysén", "Magallanes"
        )

        val communesByRegion = mapOf(
            "Arica y Parinacota" to listOf("Arica", "Camarones", "Putre", "General Lagos"),
            "Tarapacá" to listOf("Iquique", "Alto Hospicio", "Pozo Almonte", "Camiña", "Colchane", "Huara", "Pica"),
            "Antofagasta" to listOf("Antofagasta", "Mejillones", "Sierra Gorda", "Taltal", "Calama", "Ollagüe", "San Pedro de Atacama", "Tocopilla", "María Elena"),
            "Atacama" to listOf("Copiapó", "Caldera", "Tierra Amarilla", "Chañaral", "Diego de Almagro", "Vallenar", "Alto del Carmen", "Freirina", "Huasco"),
            "Coquimbo" to listOf("La Serena", "Coquimbo", "Andacollo", "La Higuera", "Paiguano", "Vicuña", "Illapel", "Canela", "Los Vilos", "Salamanca", "Ovalle", "Combarbalá", "Monte Patria", "Punitaqui", "Río Hurtado"),
            "Valparaíso" to listOf("Valparaíso", "Casablanca", "Concón", "Juan Fernández", "Puchuncaví", "Quintero", "Viña del Mar", "Isla de Pascua", "Los Andes", "Calle Larga", "Rinconada", "San Esteban", "La Ligua", "Cabildo", "Papudo", "Petorca", "Zapallar", "Quillota", "Calera", "Hijuelas", "La Cruz", "Nogales", "San Antonio", "Algarrobo", "Cartagena", "El Quisco", "El Tabo", "Santo Domingo", "San Felipe", "Catemu", "Llaillay", "Panquehue", "Putaendo", "Santa María", "Quilpué", "Limache", "Olmué", "Villa Alemana"),
            "Metropolitana de Santiago" to listOf("Santiago", "Cerrillos", "Cerro Navia", "Conchalí", "El Bosque", "Estación Central", "Huechuraba", "Independencia", "La Cisterna", "La Florida", "La Granja", "La Pintana", "La Reina", "Las Condes", "Lo Barnechea", "Lo Espejo", "Lo Prado", "Macul", "Maipú", "Ñuñoa", "Pedro Aguirre Cerda", "Peñalolén", "Providencia", "Pudahuel", "Quilicura", "Quinta Normal", "Recoleta", "Renca", "San Joaquín", "San Miguel", "San Ramón", "Vitacura", "Puente Alto", "Pirque", "San José de Maipo", "Colina", "Lampa", "Tiltil", "San Bernardo", "Buin", "Calera de Tango", "Paine", "Melipilla", "Alhué", "Curacaví", "María Pinto", "San Pedro", "Talagante", "El Monte", "Isla de Maipo", "Padre Hurtado", "Peñaflor"),
            "O'Higgins" to listOf("Rancagua", "Codegua", "Coinco", "Coltauco", "Doñihue", "Graneros", "Las Cabras", "Machalí", "Malloa", "Mostazal", "Olivar", "Peumo", "Pichidegua", "Quinta de Tilcoco", "Rengo", "Requínoa", "San Vicente", "Pichilemu", "La Estrella", "Litueche", "Marchigüe", "Navidad", "Paredones", "San Fernando", "Chépica", "Chimbarongo", "Lolol", "Nancagua", "Palmilla", "Peralillo", "Placilla", "Pumanque", "Santa Cruz"),
            "Maule" to listOf("Talca", "Constitución", "Curepto", "Empedrado", "Maule", "Pelarco", "Pencahue", "Río Claro", "San Clemente", "San Rafael", "Cauquenes", "Chanco", "Pelluhue", "Curicó", "Hualañé", "Licantén", "Molina", "Rauco", "Romeral", "Sagrada Familia", "Teno", "Vichuquén", "Linares", "Colbún", "Longaví", "Parral", "Retiro", "San Javier", "Villa Alegre", "Yerbas Buenas"),
            "Ñuble" to listOf("Chillán", "Bulnes", "Cobquecura", "Coelemu", "Coihueco", "El Carmen", "Ninhue", "Ñiquén", "Pemuco", "Pinto", "Portezuelo", "Quillón", "Quirihue", "Ránquil", "San Carlos", "San Fabián", "San Ignacio", "San Nicolás", "Treguaco", "Yungay"),
            "Biobío" to listOf("Concepción", "Coronel", "Chiguayante", "Florida", "Hualqui", "Lota", "Penco", "San Pedro de la Paz", "Santa Juana", "Talcahuano", "Tomé", "Hualpén", "Lebu", "Arauco", "Cañete", "Contulmo", "Curanilahue", "Los Álamos", "Tirúa", "Los Ángeles", "Antuco", "Cabrero", "Laja", "Mulchén", "Nacimiento", "Negrete", "Quilaco", "Quilleco", "San Rosendo", "Santa Bárbara", "Tucapel", "Yumbel", "Alto Biobío"),
            "Araucanía" to listOf("Temuco", "Carahue", "Cunco", "Curarrehue", "Freire", "Galvarino", "Gorbea", "Lautaro", "Loncoche", "Melipeuco", "Nueva Imperial", "Padre Las Casas", "Perquenco", "Pitrufquén", "Pucón", "Saavedra", "Teodoro Schmidt", "Toltén", "Vilcún", "Villarrica", "Cholchol", "Angol", "Collipulli", "Curacautín", "Ercilla", "Lonquimay", "Los Sauces", "Lumaco", "Purén", "Renaico", "Traiguén", "Victoria"),
            "Los Ríos" to listOf("Valdivia", "Corral", "Lanco", "Los Lagos", "Máfil", "Mariquina", "Paillaco", "Panguipulli", "La Unión", "Futrono", "Lago Ranco", "Río Bueno"),
            "Los Lagos" to listOf("Puerto Montt", "Calbuco", "Cochamó", "Fresia", "Frutillar", "Los Muermos", "Llanquihue", "Maullín", "Puerto Varas", "Castro", "Ancud", "Chonchi", "Curaco de Vélez", "Dalcahue", "Puqueldón", "Queilén", "Quellón", "Quemchi", "Quinchao", "Osorno", "Puerto Octay", "Purranque", "Puyehue", "Río Negro", "San Juan de la Costa", "San Pablo", "Chaitén", "Futaleufú", "Hualaihué", "Palena"),
            "Aysén" to listOf("Coyhaique", "Lago Verde", "Aysén", "Cisnes", "Guaitecas", "Cochrane", "O'Higgins", "Tortel", "Chile Chico", "Río Ibáñez"),
            "Magallanes" to listOf("Punta Arenas", "Laguna Blanca", "Río Verde", "San Gregorio", "Cabo de Hornos", "Antártica", "Porvenir", "Primavera", "Timaukel", "Natales", "Torres del Paine")
        )

        val regionAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, regions)
        regionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        regionSpinner.adapter = regionAdapter

        regionSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedRegion = regions[position]
                val communes = communesByRegion[selectedRegion] ?: emptyList()

                val communeAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, communes)
                communeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                comunaSpinner.adapter = communeAdapter
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                comunaSpinner.adapter = null
            }
        }
    }

    private fun setupClickListeners() {
        registerButton.setOnClickListener {
            if (validateInputs()) {
                registerUser()
            }
        }
    }

    private fun validateInputs(): Boolean {
        val addressRegex = Regex("^[^,]+,[^,]+(,[^,]+)?$")
        val address = addressEditText.text.toString().trim()

        if (!addressRegex.matches(address)) {
            addressEditText.error = "La dirección debe tener el formato: 'texto, texto[, texto]'"
            return false
        }

        if (!termsCheckBox.isChecked) {
            termsCheckBox.error = "Debes aceptar los términos y condiciones"
            return false
        }

        return true
    }

    private fun registerUser() {
        lifecycleScope.launch {
            try {
                val registerResponse = userClient.registerUser(registrationData.toUserRegistration())

                if (registerResponse != null) {
                    if (registerResponse.isSuccessful) {
                        // User registered, now login
                        val loginResponse = authClient.login(
                            registrationData.email,
                            registrationData.contrasena
                        )

                        if (loginResponse.isSuccessful) {
                            loginResponse.body()?.let { authResponse ->
                                tokenManager.saveAuthData(
                                    authResponse.access_token,
                                    authResponse.id,
                                    authResponse.expToken
                                )
                                navigateToMainActivity()
                            }
                        } else {
                            handleError("Error iniciando sesión")
                        }
                    } else {
                        handleError("Error en el registro")
                    }
                }
            } catch (e: Exception) {
                handleError("Error de conexión")
            }
        }
    }

    private fun UserRegistrationData.toUserRegistration(): UserRegistrationData? {
        // The base registration data already contains most fields from the first screen
        return this.telefono.takeIf { it.isNotBlank() }?.removePrefix("+")?.let {
            this.genero.takeIf { it != "Selecciona tu Género" }?.let { it1 ->
                UserRegistrationData(
                    contrasena = this.contrasena,
                    nombre = this.nombre,
                    apellido = this.apellido,
                    nombreUsuario = this.nombreUsuario,
                    email = this.email,
                    telefono = it, // Remove + prefix if present and not empty
                    genero = it1, // Only include gender if actually selected
                    rut = this.rut,
                    fechaNacimiento = this.fechaNacimiento,
                )
            }
        }
    }

    private fun handleError(errorMessage: String) {
        // We use viewLifecycleOwner.lifecycleScope to ensure we're respecting the Fragment's lifecycle
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
            try {
                // Log the error for debugging
                Log.e("UserRegisterNextFragment", "Error occurred: $errorMessage")

                // Show error message to user
                val context = context
                if (context != null) {
                    Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                }

                // Enable the register button again if it was disabled
                registerButton.isEnabled = true

                // You might want to highlight specific fields based on the error
                when {
                    errorMessage.contains("conexión", ignoreCase = true) -> {
                        // Handle connection errors
                        showNetworkErrorDialog()
                    }
                }
            } catch (e: Exception) {
                // Catch any exceptions that might occur during error handling
                Log.e("UserRegisterNextFragment", "Error in handleError: ${e.message}")
            }
        }
    }

    // Helper function to show a network error dialog
    private fun showNetworkErrorDialog() {
        context?.let { ctx ->
            MaterialAlertDialogBuilder(ctx)
                .setTitle("Error de Conexión")
                .setMessage("No se pudo conectar con el servidor. Por favor, verifica tu conexión a internet e intenta nuevamente.")
                .setPositiveButton("Reintentar") { dialog, _ ->
                    dialog.dismiss()
                    // You could add retry logic here if needed
                }
                .setNegativeButton("Cancelar") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }
    }

    private fun navigateToMainActivity() {
        val intent = Intent(requireContext(), MainActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

    companion object {
        private const val ARG_REGISTRATION_DATA = "registration_data"

        fun newInstance(registrationData: UserRegistrationData) = UserRegisterNextFragment().apply {
            arguments = Bundle().apply {
                putParcelable(ARG_REGISTRATION_DATA, registrationData)
            }
        }
    }
}