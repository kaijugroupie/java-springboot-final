package com.bolsadeideas.springboot.app.controllers;

import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.bolsadeideas.springboot.app.models.entity.Cliente;
import com.bolsadeideas.springboot.app.models.service.IProductoService;
import com.bolsadeideas.springboot.app.util.paginator.PageRender;

@Controller
@RequestMapping("/cliente")
@SessionAttributes("cliente")
public class ClienteController {

	@Autowired
	private IProductoService clienteService;

	@GetMapping(value = "/ver/{id}")
	public String ver(@PathVariable(value = "id") Long id, Map<String, Object> model, RedirectAttributes flash) {

		Cliente cliente = clienteService.findOne(id);
		if (cliente == null) {
			flash.addFlashAttribute("error", "El Cliente no existe en la base de datos");
			return "redirect:/cliente/listar";
		}

		model.put("cliente", cliente);
		model.put("titulo", "Detalle Cliente: " + cliente.getNombre());
		return "cliente/ver";
	}

	@RequestMapping(value = "/listar", method = RequestMethod.GET)
	public String listar(@RequestParam(name = "page", defaultValue = "0") int page, Model model) {

		Pageable pageRequest = new PageRequest(page, 4);

		Page<Cliente> clientes = clienteService.findAll(pageRequest);

		PageRender<Cliente> pageRender = new PageRender<Cliente>("/cliente/listar", clientes);
		model.addAttribute("titulo", "Listado de Clientes");
		model.addAttribute("clientes", clientes);
		model.addAttribute("page", pageRender);
		return "cliente/listar";
	}

	@RequestMapping(value = "/form")
	public String crear(Map<String, Object> model) {

		Cliente cliente = new Cliente();
		model.put("cliente", cliente);
		model.put("titulo", "Crear Cliente");
		return "cliente/form";
	}

	@RequestMapping(value = "/form/{id}")
	public String editar(@PathVariable(value = "id") Long id, Map<String, Object> model, RedirectAttributes flash) {

		Cliente cliente = null;

		if (id > 0) {
			cliente = clienteService.findOne(id);
			if (cliente == null) {
				flash.addFlashAttribute("error", "El ID del Cliente no existe en la base de datos.");
				return "redirect:/cliente/listar";
			}
		} else {
			flash.addFlashAttribute("error", "El ID del Cliente debe ser distinto a cero.");
			return "redirect:/cliente/listar";
		}
		model.put("cliente", cliente);
		model.put("titulo", "Editar Cliente");
		return "cliente/form";
	}

	@RequestMapping(value = "/form", method = RequestMethod.POST)
	public String guardar(@Valid Cliente cliente, BindingResult result, Model model,
			// @RequestParam("file") MultipartFile foto,
			RedirectAttributes flash, SessionStatus status) {

		if (result.hasErrors()) {
			model.addAttribute("titulo", "Formulario de Clientes");
			return "cliente/form";
		}

		String mensajeFlash = (cliente.getId() != null) ? "¡Cliente editado exitosamente!" : "¡Cliente creado exitosamente!";

		clienteService.save(cliente);
		status.setComplete();
		flash.addFlashAttribute("success", mensajeFlash);
		return "redirect:/cliente/listar";
	}

	@RequestMapping(value = "/eliminar/{id}")
	public String eliminar(@PathVariable(value = "id") Long id, RedirectAttributes flash) {

		if (id > 0) {
			clienteService.delete(id);
			flash.addFlashAttribute("success", "¡Cliente eliminado exitosamente!");
		}
		return "redirect:/cliente/listar";
	}
}
