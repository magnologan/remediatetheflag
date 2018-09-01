/*
 *  
 * REMEDIATE THE FLAG
 * Copyright 2018 - Andrea Scaduto 
 * remediatetheflag@gmail.com
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package com.remediatetheflag.portal.auth.management;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.remediatetheflag.portal.actions.IAction;
import com.remediatetheflag.portal.messages.MessageGenerator;
import com.remediatetheflag.portal.model.Organization;
import com.remediatetheflag.portal.model.Team;
import com.remediatetheflag.portal.model.User;
import com.remediatetheflag.portal.persistence.HibernatePersistenceFacade;
import com.remediatetheflag.portal.utils.Constants;

public class RemoveTeamAction extends IAction {

	private HibernatePersistenceFacade hpc = new HibernatePersistenceFacade();

	@Override
	public void doAction(HttpServletRequest request, HttpServletResponse response) throws Exception {
		User sessionUser = (User) request.getSession().getAttribute(Constants.ATTRIBUTE_SECURITY_CONTEXT);
		JsonObject json = (JsonObject) request.getAttribute(Constants.REQUEST_JSON);

		JsonElement teamIdJson = json.get(Constants.ACTION_PARAM_TEAM_ID);
		Integer teamId = teamIdJson.getAsInt();
		Team team = hpc.getTeam(teamId,sessionUser.getManagedOrganizations());
	
		List<User> users = hpc.getUsersForTeamName(team.getName(), sessionUser.getManagedOrganizations());
		if(null==users || users.isEmpty()){
			hpc.deleteTeam(team);
			MessageGenerator.sendSuccessMessage(response);
		}
		else{
			MessageGenerator.sendErrorMessage("TeamNotEmpty", response);
		}
	}
	
	public Boolean isManagingOrg(User user, Organization org) {
		for(Organization managed : user.getManagedOrganizations()) {
			if(managed.getId().equals(org.getId())) {
				return true;
			}
		}
		return false;
	}

}